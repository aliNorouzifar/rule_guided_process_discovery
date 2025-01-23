import pm4py

def conformance_checking(log_path, model_path):
    log = pm4py.read_xes(str(log_path))
    net,im,fm= pm4py.read.read_pnml(model_path)

    fitness_dict = pm4py.conformance.fitness_alignments(log,net,im,fm)
    precision = pm4py.conformance.precision_alignments(log,net,im,fm)

    return round(fitness_dict["log_fitness"],2),round(precision,2)