#!/bin/bash
sudo yum -y update
sudo amazon-linux-extras install -y java-openjdk11

APP_DIR="/home/cnam/cnam-api"

sudo useradd cnam
sudo mkdir $APP_DIR
sudo mkdir -p $APP_DIR/object-store
sudo chown -R cnam:cnam $APP_DIR

read -r -d '' SYSTEMD_UNIT <<EOL
[Unit]
Description=CNAM API
After=syslog.target network.target

[Service]
SuccessExitStatus=143

User=cnam
Group=cnam

Type=simple

Environment="APP_DIR=${APP_DIR}"
WorkingDirectory=${APP_DIR}
ExecStart=/bin/java -jar ${APP_DIR}/cnam-api-server-0.0.1-all.jar
ExecStop=/bin/kill -15 $MAINPID

[Install]
WantedBy=multi-user.target
EOL

echo "$SYSTEMD_UNIT" | sudo tee /etc/systemd/system/cnam.service > /dev/null
sudo systemctl daemon-reload
#sudo systemctl enable cnam.service
