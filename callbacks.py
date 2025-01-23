from dash import Input, Output, State, html
# from pathlib import Path
# from functions.my_functions import import_log
# from functions.EMD_based_framework import apply as PVI_apply
# from functions.explainability_extraction import apply as XPVI_apply, decl2NL
from pages.main_page import rule_src_selection,show_rule_uploder,show_Minerful_params, IMr_params_show, rule_related_statistics_show,conformance_related_statistics_show
import os
import shutil
from functions.declare.minerful_calls import discover_declare
from pathlib import Path
from functions.discovery import run_IMr
import json
from functions.functions.evaluation import conformance_checking,extract_significant_dev


UPLOAD_FOLDER = "event_logs"
OUTPUT_FOLDER = "outputs"
WINDOWS = []

def clear_upload_folder(folder_path):
    shutil.rmtree(folder_path)
    os.makedirs(folder_path)

def register_callbacks(app):
    clear_upload_folder("event_logs")
    clear_upload_folder("output_files")

    @app.callback(
        Output("output-data-upload2", "children"),
        # [Input("output-data-upload", "children")],
        [Input("upload-data", "isCompleted")],
        [State("upload-data", "upload_id")],
    )
    def parameters_PVI(isCompleted,id):
        if isCompleted==True:
            return rule_src_selection()

    # Callback to update the output based on the selected options
    @app.callback(
        Output('output-data-upload6', 'children'),
        Input('rule_src', 'value')
    )
    def update_output(rule_source):
        if rule_source=="manual":
            return show_rule_uploder()
        elif rule_source=="Minerful":
            return show_Minerful_params()
        return "Select a rule source!"

    @app.callback(
        Output('output-data-upload10', 'children'),
        Input('run_Minerful', "n_clicks"),
        State("upload-data", "upload_id"),
        State("support_val", "value"),
        State("confidence_val", "value"),
    )
    def Minerful_call(n, input_file, support, confidence):
        input_log_path = os.path.join(UPLOAD_FOLDER, input_file)
        files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
        file = Path(UPLOAD_FOLDER) / f"{input_file}" / files[0]
        output_log_path = os.path.join(OUTPUT_FOLDER, "rules.json")
        discover_declare(file, output_log_path, support, confidence)
        return IMr_params_show()

    @app.callback(
        Output("output-data-upload8", "children"),
        # [Input("output-data-upload", "children")],
        [Input("rule_upload", "isCompleted")],
        [State("upload-data", "upload_id")],
    )
    def parameters_PVI(isCompleted, id):
        if isCompleted == True:
            return IMr_params_show()

    @app.callback(
        # Output('output-data-upload3', 'children'),
        Output("gv", "dot_source"), Output("gv", "engine"),
        Input('run_IMr_selector', "n_clicks"),
        State("upload-data", "upload_id"),
        #     State("rule_upload", "upload_id"),
        State("sup_IMr_val", "value"),
    )
    def IMr_call(n2,log_file,sup):
        input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
        files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
        log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
        rule_path = os.path.join(OUTPUT_FOLDER, "rules.json")
        gviz = run_IMr(log_path,rule_path,sup)
        # pn_visualizer.view(gviz)
        # IMr_figures(gviz)
        return str(gviz), "dot"

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
        # Open and read the JSON file
        with open(os.path.join(r"output_files/", "stats.json"), "r") as file:
            data = json.load(file)
        dev_rank = extract_significant_dev(data["dev_list"])
        statistics = rule_related_statistics_show(data["N.rules"], data["N.dev"], data["support_cost"], data["confidence_cost"],dev_rank)
        return [html.P(line) for line in statistics]

    @app.callback(
        Output('output-data-upload7', 'children'),
        Input('ccf_show', "n_clicks"),
        State("upload-data", "upload_id"),
    )
    def rule_related_statistics(n3,log_file):
        input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
        files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
        log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
        model_path = os.path.join(r"output_files", "model.pnml")
        fitness, precision = conformance_checking(log_path, model_path)
        statistics = conformance_related_statistics_show(fitness, precision)
        return [html.P(line) for line in statistics]

    # @app.callback(
    #     Output('output-data-upload3', 'children'),
    #     Input('run_IMr_selector', "n_clicks"),
    #     State("upload-data", "upload_id"),
    #     State("rule_upload", "upload_id"),
    #     State("sup_IMr_val", "value"),
    # )
    # def IMr_call(n2,log_file, rule_file,sup):
    #     # input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
    #     # files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
    #     # log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
    #     # rule_path = os.path.join(OUTPUT_FOLDER, "rules.json")
    #     # gviz = run_IMr(log_path,rule_path,sup)
    #     # # pn_visualizer.view(gviz)
    #     # return IMr_figures(gviz)
    #     return "HI"











    # @app.callback(
    #     Output("output-data-upload3", "children"),
    #     Input("run_PVI", "n_clicks"),
    #     State("my-numeric-input-1", "value"),
    #     State("my-numeric-input-2", "value"),
    #     State("my-slider3", "value"),
    #     State("TF", "value"),
    #     State("TF2", "value"),
    #     State("xaxis-data", "value")
    # )
    # def plot_data(n, n_bin, w, sig, faster, export, kpi):
    #     if n>0:
    #         if kpi is not None:
    #             fig_src1,fig_src2 = PVI_apply(n_bin, w, sig, faster, export, kpi, WINDOWS)
    #             return PVI_figures(fig_src1, fig_src2)
    #
    #
    # @app.callback(
    #     Output("output-data-upload4", "children"),
    #     Input("X_parameters", "n_clicks"),
    #     )
    # def parameters_explainability(n):
    #     if n > 0:
    #         return parameters_view_explainability()
    #
    # @app.callback(
    #     Output("output-data-upload5", "children"),
    #     Input("XPVI_run", "n_clicks"),
    #     State("my-numeric-input-1", "value"),
    #     State("my-numeric-input-2", "value"),
    #     State("my-numeric-input-3", "value"),
    #     State("my-numeric-input-4", "value"),
    #     State("xaxis-data", "value")
    #     )
    # def plot_Xdata(n,n_bin, w, theta_cvg, n_clusters, kpi):
    #     if n > 0:
    #         fig_src3, fig_src4 = XPVI_apply(n_bin, w, theta_cvg, n_clusters, kpi, WINDOWS)
    #         return XPVI_figures(fig_src3, fig_src4)
    #
    # @app.callback(
    #     Output("output-data-upload6", "children"),
    #     Input("decl2NL_framework", "n_clicks"),
    #     )
    # def X2NL(n):
    #     if n > 0:
    #         return decl2NL_parameters()
    #
    # @app.callback(
    #     Output("output-data-upload7", "children"),
    #     Input("decl2NL_pars", "n_clicks"),
    #     State("cluster_number", "value"),
    #     State("segment_number", "value")
    # )
    # def X2NL_calc(n,cluster, segment):
    #     if n > 0:
    #         list_sorted, list_sorted_reverse = decl2NL(cluster, segment)
    #         return statistics_print(list_sorted, list_sorted_reverse)
