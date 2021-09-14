# Hotel Reservation System
Course work for Distributed Data Processing

## Stack

Java 8.0, Spring Boot, Hibernate, PostgreSQL, Docker, Heroku, Angular

## Local docker deploy (Windows 10)

CREATE VM

docker-machine create -d virtualbox --virtualbox-memory=4096 --virtualbox-cpu-count=4 --virtualbox-disk-size=40960 --virtualbox-no-vtx-check rsoi-first-vm

docker-machine create -d virtualbox --virtualbox-memory=4096 --virtualbox-cpu-count=4 --virtualbox-disk-size=40960 --virtualbox-no-vtx-check rsoi-second-vm

docker-machine create -d virtualbox --virtualbox-memory=4096 --virtualbox-cpu-count=4 --virtualbox-disk-size=40960 --virtualbox-no-vtx-check rsoi-third-vm

INIT SWARM

docker-machine ssh rsoi-first-vm "docker swarm init --advertise-addr <rsoi-first-vm ip>"

ADD WORKERS
  
docker-machine ssh rsoi-second-vm "docker swarm join --token <TOKEN> <rsoi-first-vm ip>"
  
docker-machine ssh rsoi-third-vm "docker swarm join --token <TOKEN> <rsoi-first-vm ip>"

VIEW NODES
  
docker-machine ssh rsoi-first-vm "docker node ls"

ENVIRONMENT
  
docker-machine env rsoi-first-vm
  
Copy last line

PUSH
  
docker logout
  
docker login
  
docker-compose build --pull
  
docker images
  
docker push darfiro/<imgname>

DEPLOY
  
docker stack deploy --with-registry-auth -c docker-compose.yml darfiro

VIEW
  
docker stack ps darfiro

ACCESS
  
<rsoi-first-vm ip>:8081 

STACK RM
  
docker stack rm darfiro

START MACHINE
  
docker-machine start rsoi-first-vm
  
docker-machine start rsoi-second-vm
  
docker-machine start rsoi-third-vm

LEAVE SWARM
  
docker swarm leave
