from dash import Input, Output, State, html
from pages.main_page import rule_src_selection,show_rule_uploder,show_Minerful_params, IMr_params_show, rule_related_statistics_show,conformance_related_statistics_show,show_petri_net,IMr_no_rules_params_show
import os
import shutil
from prolysis.calls.minerful_calls import discover_declare
from pathlib import Path
from prolysis.discovery.discovery import run_IMr
import json
from prolysis.analysis.evaluation import conformance_checking,extract_significant_dev
from prolysis.util.redis_connection import redis_client
from prolysis.rules_handling.utils import rules_from_json

UPLOAD_FOLDER = "event_logs"
OUTPUT_FOLDER = "output_files"
WINDOWS = []


def clear_upload_folder(folder_path):
    shutil.rmtree(folder_path)
    os.makedirs(folder_path)

def register_callbacks(app):
    clear_upload_folder("event_logs")
    clear_upload_folder("output_files")

    @app.callback(
        Output("output-data-upload2", "children"),
        [Input("upload-data", "isCompleted")],
        [State("upload-data", "upload_id")],
    )
    def parameters_PVI(isCompleted,id):
        if isCompleted==True:
            return rule_src_selection(), False

    # Callback to update the output based on the selected options
    @app.callback(
        Output('output-data-upload6', 'children'),
        Output('show_IMr_run1', 'data'),
        Input('rule_src', 'value')
    )
    def update_output(rule_source):
        if not os.path.exists(os.path.join(r"event_logs", "rules")):
            os.makedirs(os.path.join(r"event_logs", "rules"))
        else:
            clear_upload_folder(os.path.join(r"event_logs", "rules"))
        if rule_source=="manual":
            return show_rule_uploder(), False
        elif rule_source=="Minerful":
            return show_Minerful_params(), False
        elif rule_source=="no_rule":
            redis_client.set('rules', json.dumps([]))
            redis_client.set('dimensions', json.dumps([]))
            redis_client.set('activities', json.dumps([]))
            # return IMr_no_rules_params_show(), True
            return "", True
        return "Select a rule source!", False

    @app.callback(
        # Output('output-data-upload8', 'children'),
        Output('show_IMr_run2', 'data'),
        Input('run_Minerful', "n_clicks"),
        State("upload-data", "upload_id"),
        State("support_val", "value"),
        State("confidence_val", "value"),
    )
    def Minerful_call(n, input_file, support, confidence):
        if n>0:
            input_log_path = os.path.join(UPLOAD_FOLDER, input_file)
            files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
            file = Path(UPLOAD_FOLDER) / f"{input_file}" / files[0]
            output_log_path = os.path.join("event_logs", "rules", "rules.json")
            discover_declare(file, output_log_path, support, confidence)
            rules, activities = rules_from_json(str(output_log_path))
            redis_client.set('rules',json.dumps(rules))
            redis_client.set('dimensions',json.dumps(list(rules[0].keys()-['template', 'parameters'])))
            redis_client.set('activities', json.dumps(list(activities)))
            # return IMr_params_show(), True
            return True


###################
    @app.callback(
        # Output("output-data-upload10", "children"),
        Output('show_IMr_run3', 'data'),
        [Input("rule_upload", "isCompleted")],
        [State("upload-data", "upload_id")],
    )
    def parameters_PVI(isCompleted, id):
        if isCompleted == True:
            input_rule_path = os.path.join(UPLOAD_FOLDER, "rules")
            files = os.listdir(input_rule_path) if os.path.exists(input_rule_path) else []
            # rule_path = Path(UPLOAD_FOLDER) / f"{rule_file}" / files[0]
            rule_path = os.path.join(UPLOAD_FOLDER, "rules", files[0])
            # output_log_path = os.path.join("event_logs", "rules", "rules.json")
            rules, activities = rules_from_json(str(rule_path))
            redis_client.set('rules', json.dumps(rules))
            redis_client.set('dimensions', json.dumps(list(rules[0].keys() - ['template', 'parameters'])))
            redis_client.set('activities', json.dumps(list(activities)))
            # return IMr_params_show(),True
            return True

    @app.callback(
        Output('output-data-upload10', 'children'),
        [Input('show_IMr_run1', 'data'),
        Input('show_IMr_run2', 'data'),
        Input('show_IMr_run3', 'data')])
    def sss(f1,f2,f3):
        if f1 ==True:
            return IMr_no_rules_params_show()
        elif f2==True:
            return IMr_params_show()
        elif f3==True:
            return IMr_params_show()


    @app.callback(
        Output("petri_net1", "children"),
        Input('run_IMr_selector', "n_clicks"),
        State("upload-data", "upload_id"),
        State("sup_IMr_val", "value"),
        State("dimension", "value"),
        State("absence_thr", "value"),
    )
    def IMr_call_no_rules(n1, log_file, sup, dim, abs_thr):
        if dim==None:
            dim = ""
        if abs_thr==None:
            abs_thr=""
        input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
        files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
        log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
        rules = json.loads(redis_client.get('rules'))
        activities = json.loads(redis_client.get('activities'))
        if n1>0:
            gviz = run_IMr(log_path, sup, rules, activities, dim, abs_thr)
            return show_petri_net(gviz)
        return ""

    @app.callback(
        Output("gv", "style"),  # Update the style property of the graph
        Input("zoom-slider", "value"),  # Listen to the slider value
    )
    def update_zoom(zoom_value):
        # Dynamically update the CSS `transform: scale()` property
        return {"transform": f"scale({zoom_value})", "transformOrigin": "0 0"}

    @app.callback(
        Output('output-data-upload5', 'children'),
        Input('stats_show', "n_clicks"),
    )
    def rule_related_statistics(n3):
        if n3>0:
            # Open and read the JSON file
            with open(os.path.join(r"output_files/", "stats.json"), "r") as file:
                data = json.load(file)
            dev_rank = extract_significant_dev(data["dev_list"])
            return rule_related_statistics_show(data["N.rules"], data["N.dev"], data["support_cost"], data["confidence_cost"],dev_rank)
        return ""

    @app.callback(
        Output('output-data-upload7', 'children'),
        Input('ccf_show', "n_clicks"),
        State("upload-data", "upload_id"),
    )
    def rule_related_statistics(n4,log_file):
        if n4>0:
            input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
            files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
            log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
            model_path = os.path.join(r"output_files", "model.pnml")
            fitness, precision = conformance_checking(log_path, model_path)
            return conformance_related_statistics_show(fitness, precision)
        return ""
