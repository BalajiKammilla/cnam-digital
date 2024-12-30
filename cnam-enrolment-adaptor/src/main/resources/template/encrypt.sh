#!/bin/bash

ENCRYPTEDFILE=encrypted.xml
echo -n '<prsCnamEncryptedEnrollmentRequest><requestIdentifier>test</requestIdentifier><encryptedRequest>' > $ENCRYPTEDFILE
openssl cms -encrypt -binary -in $1 -outform DER snedai_prod.pem | base64 >> $ENCRYPTEDFILE
echo '</encryptedRequest></prsCnamEncryptedEnrollmentRequest>'  >> $ENCRYPTEDFILE
