docker build --pull --rm -f "./Dockerfile" -t gestiondeprojet-loginapp:latest "."
docker build --pull --rm -f "./Dockerfile_Node" -t gestiondeprojet-node:latest "."
docker-compose up --build