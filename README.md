Akka & RabbitMQ
=========================
## About
A simple example of using [RabbitMQ](https://www.rabbitmq.com/) with Akka. This example assumes you have a RabbitMQ running somewhere.

## Run
Run `MyConsumer` first. Then run `MyProducer`. If everything works correctly you should see a message in the console like 

```shell
> got a message: yo!!
```

### Configuration
Make sure you edit the `application.conf` to point to your running RabbitMQ instance:

```
amqp {
  host = 192.168.59.106 # using Boot2Docker here
  user = guest
  pass = guest
}
```

## If you don't want to install RabbitMQ

You can use Docker:

```shell
$ docker run -d --hostname my-rabbit --name rabbit -p 8080:15672 -p 5672:5672 rabbitmq:3-management
```