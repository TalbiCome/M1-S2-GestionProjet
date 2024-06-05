docker build --pull --rm -f "./Dockerfile_Node" -t gestiondeprojet-node:latest "."
docker run -d -p 8080:8080 gestiondeprojet-node:latest -e "HOSTNAME=node"