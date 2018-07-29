export DOCKER_USERNAME=$1
export DOCKER_PASSWORD=$2

mvn clean install
docker rmi $DOCKER_USERNAME/bankservice-server
docker rmi $DOCKER_USERNAME/bankservice-mongo

docker build -t $DOCKER_USERNAME/bankservice-server -f Dockerfile-server .
docker build -t $DOCKER_USERNAME/bankservice-mongo -f Dockerfile-mongo .

docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker push $DOCKER_USERNAME/bankservice-server
docker push $DOCKER_USERNAME/bankservice-mongo
docker logout