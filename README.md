# Announcment of Retirements and New Project (신규 프로젝트 이동 공지)

- Beluga version has been retired and moved to new project  - https://github.com/TheOpenCloudEngine/uEngine-cloud
- 벨루가 버전은 새버전인 uEngine-cloud 버전으로 업그래이드 되어 프로젝트 페이지를 이동합니다 앞으로 여기로 접속해 주십시오 - https://github.com/TheOpenCloudEngine/uEngine-cloud


# Old documentation of Beluga [![Build Status](https://travis-ci.org/TheOpenCloudEngine/beluga.png)](https://travis-ci.org/TheOpenCloudEngine/beluga)

## Demo videos

1 . Provisioning

https://vimeo.com/145980366

2 . Scaling in / out

https://vimeo.com/145980080

## GIS application demo

1. 도커 제작 & 서비스 등록 - https://vimeo.com/168302786
2. 카달로그로 부터 서비스 실행 - https://vimeo.com/168302927
3. 서비스 동적 바인딩 - https://vimeo.com/168302675
4. 어플리케이션 디플로이 - https://vimeo.com/168302924
5. 액티브 디플로이 (버전 매니지 & 롤백) - https://vimeo.com/168302824
6. 스케일 & 오토스케일 - https://vimeo.com/168302841


## 준비사항

1 . java 설치.
```
$ cd script/component
$ ./install_oracle_jdk7.sh
```

2 . docker 설치.

```
$ cd script/cluster
$ ./install_docker_daemon.sh
```

> 도커를 설치하고 나서는 ubuntu 계정에 docker 그룹권한이 할당되므로, 활성화하기 위해서는 반드시 로그아웃후 재로그인 하도록 한뒤, Beluga를 시작해야 한다.

## 설정사항

1 . Credential 입력

`conf/iaas.profile.conf` 에 `accessKey`, `credentialKey`, `endPoint` 를 입력한다.

예1)AWS

```
ec2-ap.accessKey=AAAAIAT4ZAAAAH4BBBBA
ec2-ap.credentialKey=wwww7DDDDDDamMNNJJJJJJJ7paksldjflajsfowfwh
ec2-ap.endPoint=ec2.ap-northeast-1.amazonaws.com
```

예2)Openstack
```
openstack1.accessKey=demo:demo
openstack1.credentialKey=demopass
openstack1.endPoint=http://10.0.1.11:5000/v2.0
```


2 . define설정파일에 keyPair을 설정한다.

인스턴스 관리에 사용할 keyPair는 반드시 먼저 하나 만들어두도록 한다.
beluga-aws라는 이름의 key pair를 만들었다면 beluga-aws.pem파일을 특정위치에 복사하고, `define.XXX.conf` 파일을 아래와 같이 수정한다.

```
userId=ubuntu
keyPair=beluga-aws
keyPairFile=/home/ubuntu/beluga-aws.pem
```

$ chmod 400 /home/ubuntu/beluga-aws.pem

3 . hosts 파일. (Optional)

오픈스택을 설치시 설정파일에 controller의 IP 주소대신 `controller`라는 이름을 넣었다면 Beluga에서 접근시 `controller`라는 이름으로 접근하게 된다. 그러므로 `/etc/hosts`파일에 아래 항목을 추가한다.
```
10.0.1.11  controller
```

4 . 도커 레지스트리 접근 설정 (클러스터 생성후)

beluga의 private docker registry(management노드)에 접근하기 위해서는 아래 명령을 수행한다.

클러스터를 생성해야 Management 아이피를 알수 있으므로, 이 설정은 서버를 기동하고, 클러스터를 생성한 뒤에 진행해야 한다.
```
$ cd script/docker
$ ./add_insecure_registry.sh <Management 아이피>
```

## 서비스시작/종료

### 시작
```
$cd bin
$ ./beluga start
```
### 종료
```
$cd bin
$ ./beluga stop
```

## 로그보기
```
$ tail -f logs/system.log
```


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
