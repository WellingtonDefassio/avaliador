docker command:

docker run -d --name ms-avaliador --network msbank-network -e RABBITMQ_SERVER=rabbitmqbank -e EUREKA_SERVER=eureka-server ms-avaliador