
nohup /data/programs/mesos/build/bin/mesos-slave.sh \
--master=$1 \
--port=5051 \
--hostname=$2 \
--log_dir=/data/logs/mesos \
--work_dir=/data/repo/mesos \
--disk_watch_interval=10secs \
--docker=docker \
--docker_remove_delay=3mins \
--docker_stop_timeout=0secs \
--resource_monitoring_interval=1secs \
--containerizers=docker,mesos \
> /tmp/mesos-slave.out&