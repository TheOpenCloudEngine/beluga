
### 목차
 - [Overview](#overview)
 - [Zookeeper](#zookeeper)
    - [Understand zookeeper](#understand-zookeeper)
    - [Install zookeeper](#install-zookeeper)
    - [Standalone Operation](#standalone-operation)
    - [Running Replicated ZooKeeper](#running-replicated-zookeeper)
    
## Overview



## Zookeeper

### Understand zookeeper

분산 시스템을 설계 하다보면, 가장 문제점 중의 하나가 분산된 시스템간의 정보를 어떻게 공유할것이고, 
클러스터에 있는 서버들의 상태를 체크할 필요가 있으며 또한, 
분산된 서버들간에 동기화를 위한 락(lock)을 처리하는 것들이 문제로 부딪힙니다.

이러한 문제를 해결하는 시스템을 코디네이션 서비스 시스템 (coordination service)라고 하는데, 
Apache Zookeeper가 대표적입니다. 
이 코디네이션 서비스는 분산 시스템 내에서 중요한 상태 정보나 설정 정보등을 유지하기 때문에, 
코디네이션 서비스의 장애는 전체 시스템의 장애를 유발하기 때문에, 
이중화등을 통하여 고가용성을 제공해야 합니다. 
ZooKeeper는 이러한 특성을 잘 제공하고 있는데, 그런 이유로 이미 유명한 분산 솔루션에 많이 사용되고 있습니다. 
NoSQL의 한종류인 Apache HBase, 대용량 분산 큐 시스템인 Kafka등이 그 대표적인 사례입니다.

분산 시스템을 코디네이션 하는 용도로 디자인이 되었기 때문에, 
데이타 억세스가 빨라야 하며, 
자체적으로 장애에 대한 대응성을 가져야 합니다. 
그래서 Zookeeper는 자체적으로 클러스터링을 제공하며, 
장애에도 데이타 유실 없이 fail over/fail back이 가능합니다.

``
분산 프로그램 작성의 어려움: 부분적 실패(partial failure)
``

주키퍼가 부분적 실패를 완전히 사라지게 할 수는 없지만, 부분적 실패를 안전하게 다루면서 분산 응용 프로그램을 구출할 수 있도록 도와주는 도구를 제공.

 * 주키퍼는 단순하다.

단순한 몇 개의 핵심적인 연산을 제공하는 간소화(stripped-down)된 하나의 파일시스템

이벤트와 관련된 순서화(ordering)와 통지(notification) \같은 추상화도 제공한다.

 

 * 주키퍼는 다양하게 활용된다

상호조정에 필요한 다양한 데이터 구조체와 프로토콜 구축을 위한 풍부한 프리미티브 제공

 

 * 주키퍼는 고가용성을 지원한다

클러스터상에서 동작하고, 고가용성을 보장하도록 설계됨.

 

 * 주키퍼는 느슨하게 연결된 상호작용을 제공한다

상호작용 참여자의 익명성을 보장한다. 서로의 존재나 네트워크 세부사항을 모르더라도 프로세스가 상호 발견하고 상호 소통할 수 있도록 해준다.

 

 * 주키퍼는 라이브러리다

상호조정 패턴에 대한 구현물과 구현 방법을 오픈소스로 제공

![zookeeper1](/docs/images/zookeeper/zookeeper1.jpg)

#### ZNode

ZooKeeper 가 data 를 저장하는 방법에 대하여 알아보겠습니다.

ZooKeeper 가 제공해주는 파일시스템에 저장되는 파일 하나하나를 znode 라고 부릅니다.
znode 는 unix-like 시스템에서 쓰이는 file system 처럼 node 간에 hierarchy namespace 를 가지고, 이를 /(slash)를 이용하여 구분합니다.

일반적인 file system 과 다른 부분이 있습니다. ZooKeeper 는 file 과 directory 의 구분이 없이 znode라는 것 하나만을 제공합니다. 
즉, directory 에도 내용을 적을 수 있는, directory 와 file 간의 구분이 없는 file system 이라고 할 수 있습니다.
namespace hierarchy 를 가지기 때문에 관련 있는 일들을 눈에 보이는 하나의 묶음으로 관리할 수 있으면서, 
directory 가 내용을 가질 수 있게 함으로써(혹은 file 간에 hierarchy 를 가진다고 하기도 합니다.) 
불필요한 file 을 생성해야 하는 것을 막을 수 있습니다.

![zookeeper2](/docs/images/zookeeper/zookeeper2.jpg)


### Install zookeeper

우분투에서 주키퍼를 인스톨합니다.

```
$ sudo apt-get install zookeeper
Reading package lists... Done
Building dependency tree       
Reading state information... Done
.
.
.
update-alternatives: using /etc/zookeeper/conf_example to provide /etc/zookeeper/conf (zookeeper-conf) in auto mode
```

설치가 잘 완료되었는지 확인하여 봅니다.

```
$ sudo service zookeeper status
zookeeper start/running, process 6990
```

### Standalone Operation

주키퍼를 독립형 모드로 실행시키고, znode 를 컨트롤 하는 것을 진행 해 보겠습니다.

먼저 zoo.cfg 파일을 살펴봅니다.

```
$ cat /etc/zookeeper/conf/zoo.cfg

# http://hadoop.apache.org/zookeeper/docs/current/zookeeperAdmin.html

# The number of milliseconds of each tick
tickTime=2000
# The number of ticks that the initial 
# synchronization phase can take
initLimit=10
# The number of ticks that can pass between 
# sending a request and getting an acknowledgement
syncLimit=5
# the directory where the snapshot is stored.
dataDir=/var/lib/zookeeper
# Place the dataLogDir to a separate physical disc for better performance
# dataLogDir=/disk2/zookeeper

# the port at which the clients will connect
clientPort=2181
.
.
.
```

zookeeper 를 인스톨 하면 zoo.cfg 에 기본적인 설정이 되어있습니다.

zookeeper 가 실행되기 위해 필요한 최소 옵션은 다음의 세가지입니다.

```
tickTime=2000
dataDir=/var/lib/zookeeper
clientPort=2181
```

이 설정을 그대로 유지하도록 하며 주키퍼를 재실행합니다.

```
$ sudo service zookeeper restart
zookeeper stop/waiting
zookeeper start/running, process 7780
```

실행중인 주키퍼에 접속을 시도합니다.

```
$ sudo /usr/share/zookeeper/bin/zkCli.sh -server 127.0.0.1:2181
Connecting to 127.0.0.1:2181
Welcome to ZooKeeper!
JLine support is enabled

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: 127.0.0.1:2181(CONNECTED) 0] 
```

help 로 명령어들을 볼 수 있습니다.

```
[zk: 127.0.0.1:2181(CONNECTED) 1] help
ZooKeeper -server host:port cmd args
	stat path [watch]
	set path data [version]
	ls path [watch]
	delquota [-n|-b] path
	ls2 path [watch]
	setAcl path acl
	setquota -n|-b val path
	history 
	redo cmdno
	printwatches on|off
	delete path [version]
	sync path
	listquota path
	rmr path
	get path [watch]
	create [-s] [-e] path data acl
	addauth scheme auth
	quit 
	getAcl path
	close 
	connect host:port
```

현재 설치되어있는 znode 목록을 살펴보도록 하겠습니다.

```
[zk: 127.0.0.1:2181(CONNECTED) 4] ls /
[mesos, zookeeper, marathon]
```

create 명령어를 사용하여 새로운 znode 를 생성하여 봅니다.

```
[zk: 127.0.0.1:2181(CONNECTED) 5] create /zk_test my_data
Created /zk_test
[zk: 127.0.0.1:2181(CONNECTED) 6] ls /
[mesos, zookeeper, marathon, zk_test]
```

zk_test 디렉토리가 생성 된 것을 볼 수 있습니다.
이 디렉토리의 정보를 get 을 이용하여 불러오도록 합니다.

```
[zk: 127.0.0.1:2181(CONNECTED) 7] get /zk_test
my_data
cZxid = 0x2a
ctime = Tue Mar 22 12:34:33 KST 2016
mZxid = 0x2a
mtime = Tue Mar 22 12:34:33 KST 2016
pZxid = 0x2a
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 7
numChildren = 0
```

ZNode는 node 와 node 의 data 에 관한 여러 정보를 들고 있고, 이것을 stat 이라고 부릅니다. stat 이 가지는 정보는 다음과 같습니다.

```
czxid : znode를 생성한 트랜잭션의 id
mzxid : znode를 마지막으로 수정 트랜잭션의 id
ctime : znode가 생성됐을 때의 시스템 시간
mtime : znode가 마지막으로 변경되었을 때의 시스템 시간
version : znode가 변경된 횟수
cversion : znode의 자식 node를 수정한 횟수
aversion : ACL 정책을 수정한 횟수
ephemeralOwner : 임시 노드인지에 대한 flag
dataLength : data의 길이
numChildren : 자식 node의 수
```

set 명령어를 사용하면 znode 에 할당된 정보를 변경할 수 있습니다.

```
[zk: 127.0.0.1:2181(CONNECTED) 8] set /zk_test junk
cZxid = 0x2a
ctime = Tue Mar 22 12:34:33 KST 2016
mZxid = 0x2b
mtime = Tue Mar 22 12:38:53 KST 2016
pZxid = 0x2a
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 0
[zk: 127.0.0.1:2181(CONNECTED) 9] get /zk_test
junk
cZxid = 0x2a
ctime = Tue Mar 22 12:34:33 KST 2016
mZxid = 0x2b
mtime = Tue Mar 22 12:38:53 KST 2016
pZxid = 0x2a
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 0
```

끝으로, delete 를 사용하여 znode 를 삭제하여 보도록 합니다.

```
[zk: 127.0.0.1:2181(CONNECTED) 10] delete /zk_test
[zk: 127.0.0.1:2181(CONNECTED) 11] ls /
[mesos, zookeeper, marathon]
```

### Running Replicated ZooKeeper

주키퍼로 다중 노드를 통한 이중화 구성을 해 보도록 합니다.
 
주키퍼로 이중화 구성을 할 경우에는 최소 3개 노드가 있어야 하고, 3개 이상일 경우에는 홀수 개수로 구성하는 것이 좋습니다.

[Overview](#overview) 에서 다운로드 받은 이미지를 이용하여 버츄얼 박스에서 3개의 가상머신을 생성하도록 합니다.

각각의 가상머신에 master1,master2,master3 이라 이름을 붙이고, 각 아이피 주소를 별도의 텍스트로 기록해놓도록 합니다.

```
예)
192.168.0.7 master1
192.168.0.8 master2
192.168.0.9 master3
```

myid 파일을 수정하여 서버 아이디를 설정하도록 합니다.

서버 아이디는 1 ~ 255 까지 설정할 수 있습니다.

master1,2,3 서버에 차례대로 1,2,3 값을 기입하도록 합니다.

주석처리 된 라인이 있으면 안되며 숫자 하나만 기입되어있어야 합니다.

```
master1
$ sudo vi /etc/zookeeper/conf/myid

1

master2
$ sudo vi /etc/zookeeper/conf/myid

2

master3
$ sudo vi /etc/zookeeper/conf/myid

3
```

다음은 /etc/zookeeper/conf/zoo.cfg 를 수정하도록 합니다.

모든 서버에 다음의 내용을 동일하게 추가하도록 합니다.

```
$ sudo vi /etc/zookeeper/conf/zoo.cfg

server.1=192.168.0.7:2888:3888
server.2=192.168.0.8:2888:3888
server.3=192.168.0.9:2888:3888
```

모든 서버에서 주키퍼를 재시작하도록 합니다.

```
$ sudo service zookeeper restart
stop: Unknown instance: 
zookeeper start/running, process 19329
```

정상적으로 세팅이 완료되었다면, 이제 3대의 주키퍼서버는 leader 와 follow 를 선출합니다.

각각의 서버에서 다음 명령어로 어떤 서버가 leader 이며 follow 인지 알 수 있습니다.

```
follower)

$ echo stat | nc localhost 2181 | grep Mode
Mode: follower
$ netstat -lntp -all | grep -E "3888|2888|2181"
(No info could be read for "-p": geteuid()=1000 but you should be root.)
tcp6       0      0 :::3888                 :::*                    LISTEN      -               
tcp6       0      0 :::2181                 :::*                    LISTEN      -               
tcp6       0      0 192.168.0.7:3888        192.168.0.5:34536       ESTABLISHED -               
tcp6       0      0 192.168.0.7:3888        192.168.0.2:53106       ESTABLISHED -               
tcp6       0      0 192.168.0.7:47032       192.168.0.5:2888        ESTABLISHED -  


leader)

$ echo stat | nc localhost 2181 | grep Mode
Mode: leader
$ netstat -lntp -all | grep -E "3888|2888|2181"
(No info could be read for "-p": geteuid()=1000 but you should be root.)
tcp6       0      0 :::3888                 :::*                    LISTEN      -               
tcp6       0      0 :::2181                 :::*                    LISTEN      -               
tcp6       0      0 :::2888                 :::*                    LISTEN      -               
tcp6       0      0 ::1:57984               ::1:2181                ESTABLISHED -               
tcp6       0      0 192.168.0.5:34536       192.168.0.7:3888        ESTABLISHED -               
tcp6       0      0 192.168.0.5:2888        192.168.0.2:40584       ESTABLISHED -               
tcp6       0      0 192.168.0.5:3888        192.168.0.2:33124       ESTABLISHED -               
tcp6       0      0 192.168.0.5:2888        192.168.0.7:47032       ESTABLISHED -               
tcp6       0      0 ::1:2181                ::1:57984               ESTABLISHED -               
tcp6       0      0 ::1:58008               ::1:2181                TIME_WAIT   -
```

leader follow 서버에는 없는 2888 포트가 사용중입니다. 
주키퍼에서 각각의 역할이 사용하는 포트는 다음과 같습니다.

```
2181 : client
3888 : leader
2888 : follow
```

이제 이 서버들이 실제로 fail-over 기능을 수행하는지 알아보도록하겠습니다.

현재 leader 인 서버의 가상머신을 버츄얼 박스에서 종료하도록 합니다.

![zookeeper3](/docs/images/zookeeper/zookeeper3.png)

follow 서버의 /var/log/zookeeper/zookeeper.log 에 다음의 내용이 출력되며 새로운 leader 를 선출하게 됩니다.

```
2016-03-21 22:58:33,362 - WARN  [QuorumPeer[myid=3]/0:0:0:0:0:0:0:0:2181:Follower@89] - Exception when following the leader
java.net.SocketTimeoutException: Read timed out
	at java.net.SocketInputStream.socketRead0(Native Method)
	at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
	at java.net.SocketInputStream.read(SocketInputStream.java:170)
	at java.net.SocketInputStream.read(SocketInputStream.java:141)
	at java.io.BufferedInputStream.fill(BufferedInputStream.java:246)
	at java.io.BufferedInputStream.read(BufferedInputStream.java:265)
	at java.io.DataInputStream.readInt(DataInputStream.java:387)
	at org.apache.jute.BinaryInputArchive.readInt(BinaryInputArchive.java:63)
	at org.apache.zookeeper.server.quorum.QuorumPacket.deserialize(QuorumPacket.java:83)
	at org.apache.jute.BinaryInputArchive.readRecord(BinaryInputArchive.java:108)
	at org.apache.zookeeper.server.quorum.Learner.readPacket(Learner.java:152)
	at org.apache.zookeeper.server.quorum.Follower.followLeader(Follower.java:85)
	at org.apache.zookeeper.server.quorum.QuorumPeer.run(QuorumPeer.java:740)
2016-03-21 22:58:33,363 - INFO  [QuorumPeer[myid=3]/0:0:0:0:0:0:0:0:2181:Follower@166] - shutdown called
java.lang.Exception: shutdown Follower
	at org.apache.zookeeper.server.quorum.Follower.shutdown(Follower.java:166)
	at org.apache.zookeeper.server.quorum.QuorumPeer.run(QuorumPeer.java:744)
2016-03-21 22:58:33,364 - INFO  [QuorumPeer[myid=3]/0:0:0:0:0:0:0:0:2181:NIOServerCnxn@1001] - Closed socket connection for client /0:0:0:0:0:0:0:1:48772 which had sessionid 0x353996c304d0001
2016-03-21 22:58:33,365 - INFO  [NIOServerCxn.Factory:0.0.0.0/0.0.0.0:2181:NIOServerCnxnFactory@197] - Accepted socket connection from /127.0.0.1:47600
2016-03-21 22:58:33,365 - WARN  [NIOServerCxn.Factory:0.0.0.0/0.0.0.0:2181:NIOServerCnxn@354] - Exception causing close of session 0x0 due to java.io.IOException: ZooKeeperServer not running
.
.
.
```

남은 두 서버중 새로 선출된 leader 가 있는지 명령어로 알아봅니다.

```
$ echo stat | nc localhost 2181 | grep Mode
Mode: leader
```












 

 