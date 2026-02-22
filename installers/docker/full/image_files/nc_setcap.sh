#!/bin/bash
NC_NODE_LOCATION=$(readlink -f `which node`)
setcap cap_net_bind_service=+ep $NC_NODE_LOCATION