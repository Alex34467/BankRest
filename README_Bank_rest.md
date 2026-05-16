<h1>Система Управления Банковскими Картами</h1>

<h2>Сборка и запуск</h2>
<ul>
  <li>mvn clean package</li>
  <li>docker build -t bank-rest:0.0.1 .</li>
  <li>docker-compose -f docker-compose-dev.yml up</li>
</ul> 

<h3>Путь к swagger-ui:</h3>
<h4>http://localhost:8080/api/swagger-ui/index.html</h4>
<p>openapi.yaml расположен в папке src/main/resources/static/</p>
