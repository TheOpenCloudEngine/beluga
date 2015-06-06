
nohup /data/programs/mesos/build/bin/mesos-master.sh \
--ip=$1 \
--port=5050 \
--log_dir=/data/logs/mesos \
--work_dir=/data/repo/mesos \
--quorum=1 \
--zk=$2 \
--cluster=OCE-Mesos \
> /tmp/mesos-master.out&