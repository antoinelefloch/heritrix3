
----------------------------------------------------
# git clone https://github.com/antoinelefloch/heritrix3
mv heritrix3 heritrix3-github
use Eclipse on host (I have Java8 on host)  and mvn on container (this version needs to be built with Java7)

--------------------------------------------------- dev from container (Java7) on host fs
docker run -it -v /home/lee/heritrix3-github:/home/lee/heritrix3-github -p=8443:8443 lee/my-heritrix-container:1.3 /bin/bash

cd /home/lee/heritrix3-github/

(direct way, see detailed way herebelow)
root@a4deac8383b5:/home/lee# mvn install -Dmaven.test.skip=true

--------------------- detailed way
export JAVA_HOME=/usr/lib/jvm/jdk1.7.0_80/
export PATH=/usr/lib/apache-maven-3.3.9/bin:$PATH
export JAVA_OPTS=-Xmx1500m
export MAVEN_OPTS=-Xmx1500m
export HERITRIX_HOME=/root/heritrix-3.3.0-SNAPSHOT

heritrix3-github/contrib# mvn install
heritrix3-github/common# mvn install -Dmaven.test.skip=true
heritrix3-github/modules# mvn install

  java.lang.ClassNotFoundException: org.archive.util.OneLineSimpleLayout  but success

heritrix3-github/engine# mvn install -Dmaven.test.skip=true

heritrix3-github# mvn install -Dmaven.test.skip=true    (does also the dist/target gz file)

------ deploy, run
root@a4deac8383b5:~# tar zxvf /home/lee/heritrix3-github/dist/target/heritrix-3.3.0-SNAPSHOT-dist.tar.gz
or
cd /root/heritrix-3.3.0-SNAPSHOT/
cp /home/lee/heritrix3-github/commons/target/heritrix-commons-3.3.0-SNAPSHOT.jar lib/
cp /home/lee/heritrix3-github/modules/target/heritrix-modules-3.3.0-SNAPSHOT.jar lib/

root@a4deac8383b5:~/heritrix-3.3.0-SNAPSHOT/bin# ./run-mine.sh 
$HERITRIX_HOME/bin/heritrix -a admin:admin -b /

java.lang.ClassNotFoundException: sun.security.tools.KeyTool ------- if you use Java8

----- interface
https://172.17.0.2:8443/engine    (container address)
use the interface to start jobs

see crawler-beans.cxml  config in "my-files" folder

------------------------------------ when using warc files

docker cp a4deac8383b5:/root/WEB-20170411150655316-00000-728~f894a5a7aa4d~8443.warc.gz  .

pip install bottle
pip install warcat
python3 -m warcat --output-dir out1 extract WEB-20170411150655316-00000-728~f894a5a7aa4d~8443.warc.gz

----------------------------------- when using mysql

root@a4deac8383b5:~/heritrix-3.3.0-SNAPSHOT# cp ~/.m2/repository/mysql/mysql-connector-java/5.1.12/mysql-connector-java-5.1.12.jar lib/

root@ara:~/# vi /var/log/mysql/error.log
root@ara:~/# vi /etc/mysql/my.cnf
> [mysqld]
> bind-address=0.0.0.0

/etc/init.d/mysql start

mysql> GRANT ALL ON whatever_schema.* TO 'root'@'172.17.0.2' IDENTIFIED BY 'password';
mysql> flush privileges;
(GRANT should do an implicit COMMIT)
