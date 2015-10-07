# Beluga
## 설정사항

1 . beluga의 private docker registry(management노드)에 접근하기 위해서는 아래 명령을 수행한다.

```
echo DOCKER_OPTS=\"\$DOCKER_OPTS --insecure-registry <IP Address>:5000\" | sudo tee -a /etc/default/docker && sudo service docker restart
```
2 . Credential 입력
`conf/iaas.profile.conf` 에 `accessKey`, `credentialKey`, `endPoint` 를 입력한다.

예)

```
ec2-ap.accessKey=AAAAIAT4ZAAAAH4BBBBA
ec2-ap.credentialKey=wwww7DDDDDDamMNNJJJJJJJ7paksldjflajsfowfwh
ec2-ap.endPoint=ec2.ap-northeast-1.amazonaws.com
```

3 . define설정파일에 keyPair을 설정한다.

beluga-aws라는 이름의 key pair를 만들었다면 beluga-aws.pem파일을 특정위치에 복사하고, `define.XXX.conf` 파일을 아래와 같이 수정한다.

```
userId=ubuntu
keyPair=beluga-aws
keyPairFile=/home/ubuntu/beluga-aws.pem
```

$ chmod 400 /home/ubuntu/beluga-aws.pem



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
