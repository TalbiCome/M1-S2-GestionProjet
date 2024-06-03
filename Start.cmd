docker stop registery
docker rm registery
docker stop worker1
docker rm worker1
docker stop worker2
docker rm worker2
docker stop worker3
docker rm worker3

docker build --pull --rm -f "./Dockerfile" -t gestiondeprojet-loginapp:latest "."

docker network create -d bridge lb-net

docker run -d -p 8081:8081 --net=lb-net --name registery gestiondeprojet-loginapp

docker run -d --net=lb-net --name worker1 -e HOSTNAME=worker1 gestiondeprojet-loginapp
docker run -d --net=lb-net --name worker2 -e HOSTNAME=worker2 gestiondeprojet-loginapp
docker run -d --net=lb-net --name worker3 -e HOSTNAME=worker3 gestiondeprojet-loginapp