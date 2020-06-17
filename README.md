# Usage Calculator

Getting k8s pod memory usage per hour from prometheus.

## Run

Modify application.yaml .k8s.prometheusUrl value.

java run

## Usage

http://localhost:8080/usage/${namespace}/${pod_name_prefix}/memory?date=${date}&unit=${unit}&view=${csv}

| Value        | Description                             |
| ------------- | ----------------------|
| namespace | k8s namespace |
| pod_name_prefix | k8s pod names prefix. deploy name can be used. Use regex **.*** when you want get all pods. |
| date | date to query. **yyyyMMdd** style. |
| unit | unit. T, G, M, K, B, TI, GI, MI, KI . B means byte. default value is **MI**  |
| view | return style. json, csv support. default is json. |


ex) http://localhost:8080/usage/commonapi/.%2A%5B0-9%5D/memory/excel/?date=20200326&unit=Mi

// pod 이름에 숫자가 들어간 것들만 조회 (wsh-cloudzcp-admin 같은거 빠짐)
http://localhost:8080/usage/commonapi/.%2A%5B0-9%5D/memory/excel/?date=20200427&unit=Mi