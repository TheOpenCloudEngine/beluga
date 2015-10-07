# Beluga Developer

## REST API 구현

Beluga는 외부에 REST API를 제공함으로써 원격으로 각종 제어가 가능하다.
REST API는 각종 요청의 진입점이 되며, Controller(API)가 그 역할을 담당한다.
API는 요청에 필요한 사항들을 모아놓은 BaseAPI 를 상속받아서 구현되며, JAXRS를 사용하였다.
아래는 AppsAPI에 구현되어 있는 App을 디플로이 하는 코드이다.

```
@POST
@Path("/")
public Response deployApp(@PathParam("clusterId") String clusterId, Map<String, Object> data) throws Exception {
    String appId = (String) data.get("id");
    String command = (String) data.get("cmd");
    if (command != null) {
        return runApp(clusterId, appId, data, false);
    }
    return deployOrUpdateApp(clusterId, appId, data, false);
}
```

## Action

내부 작업은 대부분 RunnableAction 을 상속받는 Action클래스가 담당하며, ActionRequest 가 쌍이 되어 동작한다.
요청은 ActionRequest 를 상속받는 클래스를 통해 이루어지며 아래는 간단한 예이다.

```
DeployWebAppActionRequest request = new DeployWebAppActionRequest(clusterId, appId, webAppFile, webAppType, null, cpus, memory, scale);
DeployWebAppAction action = new DeployWebAppAction(request);
action.setCallback(new RemoveRequestMapCallback(actionRequestStatusMap));
ActionStatus status = action.getStatus();
action.run();
status.waitForDone();
```

하지만 실제 가루다 내부에서는 위와 같이 바로 action을 실행하는 방식이 아닌, ActionService를 통해 실행하도록 구현되어 있다.


```
DeployWebAppActionRequest request = new DeployWebAppActionRequest(clusterId, appId, webAppFile, webAppType, port, cpus, memory, scale, isUpdate);
ActionStatus actionStatus = actionService().request(request);
actionStatus.waitForDone();

Response response = (Response) actionStatus.getResult();
```

## Task
여러개의 동시작업이 필요할때 Task를 이용할 수 있다.
예를들어, 2~3개의 서버에 동일한 ssh 명령을 요청해야 할때, 하나씩 처리하기에는 많은 시간이 소요되므로, 동시에 실행할 수 있는 방법이 필요하다.
Task는 Todo라는 개별작업을 하나씩 추가하여 최종적으로 실행하게 한다.
사용시는 Todo를 상속받아서 실행로직을 doing() 이라는 메소드안에 구현하면 된다.

아래는 Mesos 마스터 서버를 Task를 통해 동시에 설정하는 코드의 예이다.

```
Task masterTask = new Task("configure mesos-masters");

for (final CommonInstance master : topology.getMesosMasterList()) {
    final String instanceName = master.getName();
    final String ipAddress = master.getPublicIpAddress();
    final SshInfo sshInfo = new SshInfo().withHost(ipAddress).withUser(userId).withPemFile(keyPairFile).withTimeout(timeout);
    final File scriptFile = ScriptFileNames.getClusterScriptFile(environment, ScriptFileNames.CONFIGURE_MESOS_MASTER);

    masterTask.addTodo(new Todo() {
        @Override
        public Object doing() throws Exception {
            int seq = sequence() + 1;
            logger.info("[{}/{}] Configure instance {} ({}) ..", seq, taskSize(), instanceName, ipAddress);
            MesosMasterConfiguration mesosConf = conf.clone();
            SshClient sshClient = new SshClient();
            try {
                sshClient.connect(sshInfo);
                mesosConf.withMesosClusterName(mesosClusterName).withQuorum(quorum).withZookeeperId(seq);
                mesosConf.withHostName(master.getPublicIpAddress()).withPrivateIpAddress(master.getPrivateIpAddress());
                int retCode = sshClient.runCommand(instanceName, scriptFile, mesosConf.toParameter());
                logger.info("[{}/{}] Configure instance {} ({}) Done. RET = {}", seq, taskSize(), instanceName, ipAddress, retCode);
            } finally {
                if (sshClient != null) {
                    sshClient.close();
                }
            }
            return null;
        }
    });
}

masterTask.start();

TaskResult masterTaskResult = masterTask.waitAndGetResult();
if (masterTaskResult.isSuccess()) {
    logger.info("{} is success.", masterTask.getName());
}
```
