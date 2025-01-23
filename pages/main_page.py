from dash import html, dcc
import dash_bootstrap_components as dbc
import dash_uploader as du
import dash_daq as daq
import json
import pandas as pd
import dash_interactive_graphviz

def load_variables():
    try:
        with open("output_files/internal_variables.json", "r") as json_file:
            data = json.load(json_file)
    except FileNotFoundError:
        return "No data file found."
    df = pd.read_json(data["df"], orient="split")
    data["df"] = df
    return data

def create_layout():
    return dbc.Container(
        className="page-container",
        children=[
            # Navigation Links
            html.Div(
                className="nav-links",
                children=[
                    dcc.Link("Introduction", href="/", className="nav-link"),
                    dcc.Link("IMr", href="/main", className="nav-link"),
                    dcc.Link("About Me", href="/about_me", className="nav-link"),
                ],
            ),
            # Tool Name and Description
            html.Div(
                className="tool-name-container",
                children=[
                    html.H1("Rule-guided Process Discovery", className="tool-name"),
                    html.P(
                        "A cutting-edge tool for discovering process models from event logs considering rules as input.",
                        className="tool-subtitle",
                    ),
                ],
            ),
            # Main Content Area
            html.Div(
                className="flex-container",
                children=[
                    # Left Panel: Parameter Settings
                    create_left_panel(),
                    # Right Panel: Visualization Blocks
                    create_right_panel(),
                ],
            ),
        ],
    )


def create_left_panel():
    return html.Div(
        id="left-panel",
        className="left-panel-container",
        children=[
            # Header for Parameters Settings
            html.Div(
                className="panel-header",
                children=html.H4("Parameters Settings", className="panel-title"),
            ),
            # Upload Section
            html.Div(
                className="upload-container",
                children=[
                    html.Div(
                        className="section-header",
                        children=html.H4("Upload the Event Log", className="section-title"),
                    ),
                    get_upload_component("upload-data"),
                ],
            ),
            # Parameters Section
            html.Div(
                className="parameters-wrapper",
                children=[
                    html.Hr(),

                    html.Div(id="output-data-upload2", className="parameter-block card"),
                    # html.Hr(),
                    html.Div(id="output-data-upload4", className="parameter-block card"),
                    # html.Hr(),
                    html.Div(id="output-data-upload6", className="parameter-block card"),
                    html.Div(id="output-data-upload8", className="parameter-block card"),
                    html.Div(id="output-data-upload10", className="parameter-block card"),
                ],
            ),
        ],
    )


def create_right_panel():
    return html.Div(
        id="right-panel",
        className="right-panel-container",
        children=[
            html.Div(
                className="panel-header",
                children=html.H4("Visualizations and Reports", className="panel-title"),
            ),
            html.Div(
                className="visualization-wrapper",
                children=[
                    html.Div(id="output-data-upload3", className="visualization-block"),
                    html.Hr(),
                    html.Div(
                        className="visualization-wrapper",
                        children=[
                            # Graph and slider container
                            html.Div(
                                className="graph-slider-container",
                                style={"display": "flex", "alignItems": "center"},
                                children=[
                                    # Zoom slider on the left
                                    html.Div(
                                        className="slider-container",
                                        style={"width": "10%", "marginRight": "10px"},
                                        children=[
                                            dcc.Slider(
                                                id="zoom-slider",
                                                min=1.0,  # Minimum zoom level
                                                max=3.0,  # Maximum zoom level
                                                step=0.1,  # Increment steps
                                                value=1.0,  # Default zoom level
                                                marks={i: f"{i:.1f}" for i in [1.0, 1.5, 2.0,2.5,3.0]},
                                                vertical=True,  # Make it vertical
                                            ),
                                        ],
                                    ),
                                    # Graph visualization on the right
                                    html.Div(
                                        className="graph-container",
                                        style={
                                            "flexGrow": 1,
                                            "border": "1px solid #ddd",
                                            "height": "500px",
                                            "position": "relative",
                                            "overflow": "hidden",
                                        },
                                        children=[
                                            dash_interactive_graphviz.DashInteractiveGraphviz(
                                                id="gv",
                                                style={"transform": "scale(1)", "transformOrigin": "0 0"},
                                            ),
                                        ],
                                    ),
                                ],
                            ),
                    html.Hr(),
                    html.Button(id="stats_show", children="Show statistics",
                                className="btn-secondary", n_clicks=0),
                    html.Hr(),
                    html.Div(id="output-data-upload5", className="visualization-block"),
                    html.Button(id="ccf_show", children="Show conformance checking results",
                                        className="btn-secondary", n_clicks=0),
                    html.Hr(),
                    html.Div(id="output-data-upload7", className="visualization-block"),
                ],
            ),
        ],
    )])

def get_upload_component(id):
    return du.Upload(
        id=id,
        max_file_size=500,
        chunk_size=100,
        max_files=1,
        filetypes=["xes"],
        upload_id="event_log",
    )

def rule_src_selection():
    return html.Div([
        html.H4("What is the rule source?:",className="parameter-name"),
        dcc.RadioItems(
            id='rule_src',
            options=[
                {'label': 'Discover with Minerful!', 'value': 'Minerful'},
                {'label': 'Import from local drive!', 'value': 'manual'},
            ],
            value=["manual"],  # Default selected values
            inline=True  # Display options inline
        )
    ])


def show_rule_uploder():
    return html.Div(
                className="upload-container",
                children=[
                    html.Div(
                        className="section-header",
                        children=html.H4("Upload the Rules", className="section-title"),
                    ),
                    du.Upload(
                        id="rule_upload",
                        max_file_size=500,
                        chunk_size=100,
                        max_files=1,
                        filetypes=["json"],
                        upload_id="rules",
                    )
                ],
            )


def show_Minerful_params():
    return html.Div([
        html.Div([
            html.Div(
                className="parameter-container",
                children=[
                    html.Div(
                        className="section-header",
                        children=html.H4("Minerful Parameters", className="section-title"),
                    ),
                    html.Hr(),
                    html.H4("support:", className="parameter-name"),
                    html.Div([
                        daq.NumericInput(
                            id='support_val',
                            min=0,
                            max=1,
                            value=0.005,
                            size=200
                        ),
                        html.Div(id='numeric-input-output-1')
                    ]),
                    html.Hr(),
                    html.H4("confidence:", className="parameter-name"),
                    html.Div([
                        daq.NumericInput(
                            id='confidence_val',
                            min=0,
                            max=1,
                            value=0.99,
                            size=200
                        ),
                        html.Div(id='numeric-input-output-2')
                    ]),
                    html.Hr(),
                    html.Button(id="run_Minerful", children="Run Minerful", className="btn-primary", n_clicks=0),
                    # html.Hr(),
                ]
            )
        ],
            className="flex-column align-center")
    ])



def IMr_params_show():
    return html.Div([
        html.Div([
            html.Div(
                className="parameter-container",
                children=[
                    html.Div(
                        className="section-header",
                        children=html.H4("IMr Parameters", className="section-title"),
                    ),
                    html.Hr(),
                    html.H4("IMr sup:", className="parameter-name"),
                    html.Div([
                        daq.NumericInput(
                            id='sup_IMr_val',
                            min=0,
                            max=1,
                            value=0.2
                        ),
                    ]),
                    html.Hr(),
                    html.Button(id="run_IMr_selector", children="Run IMr", className="btn-primary", n_clicks=0),
                    # html.Hr(),
                ]
            )
        ],
            className="flex-column align-center")
    ])

def rule_related_statistics_show(N_rules, N_dev, support_cost,confidence_cost, dev_list):
    return [
        f"The Number of rules used in the discovery is: {N_rules}",
        f"The Number of deviating rules considering the discovered model is: {N_dev}",
        f"The Number of deviation ratio considering the discovered model and support is: {support_cost}",
        f"The Number of deviation ratio considering the discovered model and confidence is: {confidence_cost}"
        f"The most significant deviations (based on support cost), maximum 10 of them: {dev_list}"
    ]

def conformance_related_statistics_show(fit, prc):
    return [
        f"The fitness value is: {fit}",
        f"The precision value is: {prc}",
    ]

