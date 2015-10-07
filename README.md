# Beluga

## API

### Create cluster
##### Request
```
POST /v1/clusters
{
    "id":"$CLUSTER_ID"
    ,"definition" :"$DEFINITION_ID"
}
```
##### Response
```
{
  "id": "55936840",
  "actionName": "CreateClusterAction",
  "startTime": "2015-08-11 02:04:22",
  "completeTime": "",
  "error": "",
  "state": "in-progress",
  "result": null,
  "step": {
    "stepMessages": [
      "Create instances.",
      "Configure mesos-master.",
      "Configure mesos-slave."
    ],
    "currentStep": 1,
    "totalStep": 3
  }
}
```


### Get cluster information
##### Request
```
GET /v1/clusters/$CLUSTER_ID
```
##### Response
```
{
  "clusterId": "test-cluster",
  "definitionId": "ec2-dev",
  "iaasProfile": "ec2-ap",
  "belugaMasterList": [],
  "proxyList": [
    {
      "instanceId": "i-a1119e53",
      "name": "proxy",
      "publicIpAddress": "52.68.112.137",
      "privateIpAddress": "172.31.4.58",
      "state": "running"
    }
  ],
  "mesosMasterList": [
    {
      "instanceId": "i-a9119e5b",
      "name": "mesos-master",
      "publicIpAddress": "52.69.206.78",
      "privateIpAddress": "172.31.2.235",
      "state": "running"
    }
  ],
  "mesosSlaveList": [
    {
      "instanceId": "i-6c129d9e",
      "name": "mesos-slave",
      "publicIpAddress": "52.68.87.239",
      "privateIpAddress": "172.31.10.161",
      "state": "running"
    }
  ],
  "managementList": [
    {
      "instanceId": "i-6f129d9d",
      "name": "management",
      "publicIpAddress": "54.64.151.133",
      "privateIpAddress": "172.31.5.188",
      "state": "running"
    }
  ],
  "serviceNodeList": [],
  "marathonEndPoints": [
    "http://52.69.206.78:8080"
  ],
  "registryAddressPort": "54.64.151.133:5000",
  "registryEndPoint": "http://54.64.151.133:5000"
}
```


### Deploy app
##### Request
```
GET /v1/clusters/$CLUSTER_ID
```
##### Response
```

```
