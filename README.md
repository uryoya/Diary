# Diary
株式会社ファンコミュニケーションズ インターン制作物

## 事前に必要なもの
- MySQL Server
- Redis Server
- mysql-connector-java-(version)-bin.jar
    * MySQLのリポジトリからダウンロードして `/lib/` に配置する。
- `/src/main/resources/application.conf`
    * 同ディレクトリ内にある `application.conf.example` を改名してコピーし、適切に設定する。
    
## コンパイルと実行
1. プロジェクトルートで `sbt assembly` を実行
2. `/target/scala-2.12/Diary-assembly-1.0.jar` が生成されるので、 `java -jar that.jar` で実行する。