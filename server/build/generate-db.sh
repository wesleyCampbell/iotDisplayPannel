#! /bin/bash

# Note that the PROJ_DIR variable must be set to the server root directory prior to running this script.

if [[ -z "${PROJ_DIR}" ]]; then
	echo "The PROJ_DIR variable must be set to the path to the server root prior to running this script."
	exit 1
fi

# PROJ_DIR=$1
DB_DIR="$PROJ_DIR/db"
PACKAGE=""

source "$DB_DIR/jooq.conf"

jooq_user=$user
jooq_password=$password

for db in $DB_DIR/*; do
	# Make sure file is directory
	if [ ! -d $db ] ; then
		continue
	fi
	name=$(basename "$db")

	echo "$DB_DIR"
	echo "$jooq_user"

	echo ">>> Initializing database $name..."

	source "$db/db.properties"

	db_url="jdbc:mariadb://$host:$port/$name"

	echo ">>> Running Flyway database migrations..."
	mvn flyway:migrate \
		-Dflyway.url=$db_url \
		-Dflyway.locations=filesystem:$db \
		-Dflyway.user=$user \
		-Dflyway.password=$password 

	echo ">>> Generating Java Database Object classes..."
	mvn jooq-codegen:generate \
		-Ddb.name=$name \
		-Ddb.url=$db_url \
		-Djooq.package=jooq.$name \
		-Djooq.output=target/generated-sources/ \
		-Ddb.user=$jooq_user \
		-Ddb.password=$jooq_password

	echo ">>> Finished initializing database $name."
done
