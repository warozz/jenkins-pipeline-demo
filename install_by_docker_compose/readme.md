## Steps to run

Build image
```
$docker compose up -d
```

execute bash shell
```
docker exec -it -u 0 agent_jdk11 bash
```

Generate public key
```
ubuntu@desktop:~$ ssh-keygen -f ~/.ssh/jenkins_agent_key
Generating public/private rsa key pair.
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved in /home/ubuntu/.ssh/jenkins_agent_key
Your public key has been saved in /home/ubuntu/.ssh/jenkins_agent_key.pub
The key fingerprint is:
+---[RSA 3072]----+
|  o+             |
| ...o  .         |
|  .o .+ .        |
|    o+.+ o o     |
|  ... o.So* .    |
|  o+ = +.X=      |
| o oO + *..+     |
|. oo.o o .E .    |
| o... oo.. o     |
+----[SHA256]-----+
```

Copy private key to Jenkins Master
Copy public key to Jenkins agent compose

Set
Remote Directory : /home/jenkins/agent
Java Path : /opt/java/openjdk/bin