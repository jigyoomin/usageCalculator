# Usage Calculator

Getting k8s pod memory usage per hour from prometheus.

## Run

Modify application.yaml .k8s.prometheusUrl value.

java run

## Usage

http://localhost:8080/usage/${namespace}/${pod_name_prefix}/memory?date=${date}&unit=${unit}

| Value        | Description           |
| ------------- |
| namespace | k8s namespace |
| pod_name_prefix | k8s pod names prefix. deploy name can be used. Use regex **.*** when you want get all pods. |
| date | date to query. **yyyyMMdd** style. |
| unit | unit. T, G, M, K, B, TI, GI, MI, KI . B means byte. default value is **MI**  |
