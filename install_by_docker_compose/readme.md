## Steps to run

1. Build image
```
$docker compose up -d
```
For view Jenkins log
```
$ docker logs jenkins | less
```

execute bash shell
```
docker exec -it -u 0 agent_jdk11 bash
```

Generate public key
```
ssh-keygen -t rsa -f jenkins_agent
```

Copy private key to Jenkins Master
Copy public key to Jenkins agent compose

Set
Remote Directory : /home/jenkins/agent
Java Path : /opt/java/openjdk/bin