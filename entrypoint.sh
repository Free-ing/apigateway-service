#!/bin/bash

# 퍼블릭 IP 가져오기 (AWS 메타데이터 서비스에서 퍼블릭 IP를 가져옴)
export PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)

# 로그로 퍼블릭 IP 출력 (디버깅용)
echo "Public IP: $PUBLIC_IP"

# 만약 PUBLIC_IP가 비어 있다면 기본값으로 로컬 IP(127.0.0.1)를 설정
if [ -z "$PUBLIC_IP" ]; then
  export PUBLIC_IP="127.0.0.1"
  echo "No public IP found, setting to default: $PUBLIC_IP"
fi

# Spring Boot 애플리케이션 실행
java -jar /app/your-app.jar
