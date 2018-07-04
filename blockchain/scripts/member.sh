geth --datadir=member --port 30303 --rpc --rpcaddr 10.0.0.165 --rpcport 8545 --rpccorsdomain "*" --rpcapi "eth,net,web3,personal" --ipcdisable --bootnodes=enode://4ac77627e4236535c8778b66b0e1c440b190642eb7bd01a18c49f6a0893ebc8009f3d1712d7a5f61322c26be2f888e3baf5c713a394f7b6d7bfd67fa2f6e1dfd@[127.0.0.1]:30301  >>member.log 2>&1 &

