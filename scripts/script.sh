mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/id_rsa \
    D:/PROGRAMMING/Java/Noti/target/Noti-0.0.1-SNAPSHOT.jar\
    root@92.246.143.70:/root/

echo 'Restart server...'

ssh -i ~/.ssh/id_rsa root@92.246.143.70 << EOF

pgrep java | xargs kill -9
nohup java -jar Noti-0.0.1-SNAPSHOT.jar > log.txt &

EOF

echo 'Bye'