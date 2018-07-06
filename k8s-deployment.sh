#!/bin/bash

image=$(docker images | grep k8sakkaclustersample | grep latest | awk '{print $3}')

#you must use the IMAGE ID of your local instance until lightbend implenents the aws docker login in rp!
rp generate-kubernetes-resources $image \
  --generate-all \
  --ingress-annotation ingress.kubernetes.io/rewrite-target=/ \
  --ingress-annotation nginx.ingress.kubernetes.io/rewrite-target=/ \
  --service-type LoadBalancer \
  --version 1 \
  --registry-use-local \
  --pod-controller-replicas 5 > ./deployment.yaml

sed -i -e "s|${image}|XXXX.dkr.ecr.us-east-1.amazonaws.com.k8sakkaclustersample:latest|g" deployment.yaml # Change your repo here back again. You must replace the image while the aws login isn't working in rp!
#kubectl apply -f ./deployment.yaml