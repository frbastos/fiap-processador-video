# Comando para subir o rabitmq
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.13.7-management

# Aceesar painel administrativo do rabbitmq
http://localhost:15672
user: guest
password: guest