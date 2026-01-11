.PHONY: deploy destroy build copy-plugin

deploy:
	docker compose up -d

destroy:
	docker compose down

build:
	mvn clean package

restart:
	docker compose restart

copy:
	rm -f dev-server/plugins/SebUtils/SebUtils-2.0.0.jar
	cp target/SebUtils-2.0.0.jar dev-server/plugins/SebUtils/

test: build copy restart
