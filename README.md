
# **Rule-guided Process Discovery (IMr)**
A cutting-edge tool for process analysis and variant detection, designed to help uncover control-flow variability, inefficiencies, and undesired behaviors in processes.

---

## **Table of Contents**
1. [Introduction](#introduction)
2. [Features](#features)
3. [Prerequisites](#prerequisites)
4. [Installation](#installation)
5. [Usage](#usage)
6. [Run with Docker](#run-with-docker)
7. [Sample Event Log](#sample-event-log)
8. [Contact](#contact)

---
## **Introduction**

Traditional process discovery techniques often rely solely on event logs, neglecting valuable domain knowledge and process rules. IMr bridges this gap by integrating such rules, enabling the discovery of models that are both conformant to observed behavior and aligned with expert knowledge. Key benefits include:

*   **Improved Model Quality:** Generates process models that better reflect real-world constraints and domain expertise.
*   **Enhanced Interpretability:** Creates more understandable models by incorporating explicit process rules.
*   **Increased Applicability:** Produces models that are better suited for conformance checking, performance analysis, and other process mining applications.

---

## **Features**

*   Integrates discovered or user-defined process rules into the process discovery workflow.
*   Employs a divide-and-conquer strategy, using rules to guide the selection of process structures at each recursion step.
*   Supports the discovery of high-quality imperative process models, such as BPMN models and Petri nets.
*   Evaluates the quality of discovered models based on conformance metrics and rule alignment.
*   Supports user uploads of event logs and rule sets.

---

## **Prerequisites**
- **Python 3.11**
- **Dependencies**: Listed in `requirements.txt` (if not using Docker)

---

## **Installation**
1. Clone the repository:
   ```bash
   git clone https://github.com/aliNorouzifar/rule_guided_process_discovery
   ```
2. Navigate to the project directory
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

---

## **Usage**
1. Run the tool:
   ```bash
   python app.py
   ```
2. Open your web browser and navigate to:
   ```
   http://127.0.0.1:8002/
   ```
3. Use the provided navigation links to explore the tool:
   - Upload an event log for analysis.
   - Download the sample event log for testing.

---

## **Run with Docker**
If you prefer to run the tool without installing dependencies, you can use the provided Docker image:

1. Pull the Docker image from the GitHub Container Registry:
   ```bash
   docker pull ghcr.io/alinorouzifar/imr:latest
   ```

2. Run the Docker container:
   ```bash
   docker run -p 8002:8002 ghcr.io/alinorouzifar/imr:latest
   ```

3. Open your web browser and navigate to:
   ```
   http://127.0.0.1:8002/
   ```

The tool will now be accessible without the need to install Python or dependencies on your system.

---

## **Contact**
For questions, feedback, or collaborations, feel free to reach out:

- 📧 **Email**: [ali.norouzifar@pads.rwth-aachen.de](mailto:ali.norouzifar@pads.rwth-aachen.de)
- 💼 **LinkedIn**: [LinkedIn Profile](https://www.linkedin.com/in/ali-norouzifar/)
