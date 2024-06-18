# vs_lab03
Labor 3 Verteilte Systeme

Local IP:

192.168.0.112

/home/student/Repositories/vs_lab03/echo/target/echo-1.0-SNAPSHOT.jar

/home/student/Repositories/vs_lab03/mynet_192_168_0_112.json

/home/student/.ssh/id_rsa

/home/student/echo.jar

# Aenderungen

sudo nano /etc/ssh/sshd_config
Add line:
PubkeyAcceptedAlgorithms +ssh-rsa
sudo systemctl restart sshd