#!/usr/bin/env bash

kubectl create secret generic zetes-encryption-certificate -n mobis --from-file=./zetes-encryption-certificate.pem