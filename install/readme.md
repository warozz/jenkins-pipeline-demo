## Steps to run

Build image
```
$docker image build -t my-jenkins .
```

Create container
```
$docker container run -d -u 0:0 --group-add $(stat -c '%g' /var/run/docker.sock) \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -p 8080:8080 -p 50000:50000 \
  -v $(pwd)/jenkins:/var/jenkins_home \
  --name jenkins my-jenkins
$docker container ps
$docker container logs jenkins --follow
```


## Step to add Agent

```
$ docker pull jenkinsci/ssh-slave

$ docker run -d --restart=always     -p 127.0.0.1:2376:2375     -v /var/run/docker.sock:/var/run/docker.sock     alpine/socat     tcp-listen:2375,fork,reuseaddr unix-connect:/var/run/docker.sock
```
