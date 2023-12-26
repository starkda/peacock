КАК ЗАПУСКАТЬ:
Проект запускался на 19 java. Есть шанс что на других версиях будет превышен лимит по памяти
1) создаем jar файл: mvn clean package
Сгенерится 2 jar файла. понадобится peacock-1.0-SNAPSHOT-jar-with-dependencies.jar
2) запускаем: java -Xmx1G -jar target/peacock-1.0-SNAPSHOT-jar-with-dependencies.jar "файл.txt"
Путь к файлу прописывается от корневой папки проекта.
