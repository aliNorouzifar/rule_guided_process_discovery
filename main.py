import json
import pandas as pd
import pm4py
from automata.fa.dfa import DFA
from functions import discovery
from pm4py.objects.log.importer.xes import importer as xes_importer
from pm4py.visualization.petri_net import visualizer as pn_visualizer
from pm4py.objects.petri_net.utils.reachability_graph import construct_reachability_graph
from pm4py.objects.log import obj as log_instance
from functions.analysis import gui
import ast
import os
import time
from pathlib import Path
from functions.utils import rules_from_json, preprocess, dfa_list_generator




support, LPlus_LogFile, rules_path= gui.input()


dim = 'support'
lookup_table_path = Path("files") / "lookup_table.csv"
event_log_xes = xes_importer.apply(LPlus_LogFile)
logM = log_instance.EventLog()
logM.append(log_instance.Trace())


rules, activities = rules_from_json(rules_path)
rules_proccessed, absence_list = preprocess(rules)

event_log_xes = pm4py.filter_event_attribute_values(
event_log_xes,
attribute_key="concept:name",  # Default attribute for activity names in XES
values=absence_list,
retain=False  # Retain=False means we exclude the specified activities
)

print('rules are preprocessed')

lookup_table = pd.read_csv(lookup_table_path, sep=';', index_col=0)
lookup_table=  lookup_table.map(lambda x: ast.literal_eval(x) if isinstance(x, str) else x)


S_mapped = declare_processing.assign_alphabet(activities)

print('conversion or rules to DFAs started')
dfa_list, total_sup, total_conf = dfa_list_generator(rules_proccessed, S_mapped)
print('conversion or rules to DFAs ended')


print(f"_______________ support is {support}_____________________")
print('process discovery started')
start = time.time()
net, initial_marking, final_marking = discovery.apply_bi(event_log_xes,logM, sup=support, ratio=0, size_par=1,rules = (rules_proccessed,lookup_table))
end = time.time()
print(end-start)
print('process discovery ended')


pm4py.write_pnml(net, initial_marking, final_marking, os.path.join(r"outputs",f"IMr_{round(10*support)}.pnml"))

parameters = {pn_visualizer.Variants.WO_DECORATION.value.Parameters.FORMAT:"pdf"}
gviz = pn_visualizer.apply(net, initial_marking, final_marking, parameters=parameters)
pn_visualizer.view(gviz)

print('model_checking started')
rg = construct_reachability_graph(net, initial_marking, use_trans_name=False, parameters=None)
aa = declare_processing.reachability2NFA(rg,activities)
model_dfa = DFA.from_nfa(aa)
cond = []
sup_cost = 0
conf_cost = 0
for dfa in dfa_list:
    constraint_dfa_complement = dfa[1].complement()
    intersection_dfa = model_dfa.intersection(constraint_dfa_complement)
    if not intersection_dfa.isempty():
        sup_cost += dfa[2]
        conf_cost += dfa[2]
    cond.append((dfa[0],intersection_dfa.isempty(),dfa[2],dfa[3]))
print('model_checking ended')

report = {}
report['time'] = end-start
report['N.rules'] = len(dfa_list)
report['N.dev'] = len([(x[0][0],x[0][1]) for x in cond if x[1]==False])
report['support_cost'] = round(sup_cost/total_sup,2)
report['confidence_cost'] =round(conf_cost/total_conf,2)
report['dev_list'] = [(x[0][0],x[0][1],round(x[2],2),round(x[3],2)) for x in cond if x[1]==False]

with open(os.path.join(r"outputs/",f"IMr_{round(10*support)}.json"), "w") as json_file:
    json.dump(report, json_file, indent=4)

print('The report is generated')


