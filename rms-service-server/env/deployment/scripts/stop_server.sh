#!/bin/bash

message=`curl http://localhost/mng/stop`

sleep 3s

if [ "$message" = "success" ]; then
  echo "[ersReservationAppliction]STOP SUCCESS"
  exit 0
else
  echo "[ersReservationAppliction]STOP ERROR!!"
  exit 1
fi
