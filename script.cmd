docker build --pull --rm -f "./Dockerfile" -t gestiondeprojet-loginapp:latest "."

docker save -o gestiondeprojet-loginapp.tar gestiondeprojet-loginapp:latest

docker build --pull --rm -f "./Dockerfile.node" -t gestiondeprojet-node:latest "."

docker-compose up --build