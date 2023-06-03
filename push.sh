#!/bin/bash

if [[ -z $1 ]]; then
  echo "Usage: ./push.sh <version>"
  exit 1
fi

docker build -t emortalmc/minesweeper:$1 .
docker tag emortalmc/minesweeper:$1 ghcr.io/emortalmc/minesweeper:$1
docker push ghcr.io/emortalmc/minesweeper:$1
