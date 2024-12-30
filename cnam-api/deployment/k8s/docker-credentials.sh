#! /usr/bin/env bash

kubectl -n mobis create secret generic docker-credntials \
		--from-file=.dockerconfigjson=docker-config.json \
		--type=kubernetes.io/dockerconfigjson