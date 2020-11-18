# ProjetI4-common
 common library for projetI4


** Documentation

* types

Network formatted string : this is the layout of strings sent over the network
<============ len+4 ==============>
+---------+-----------------------+
| Int len |   Byte[] characters   |
+---------+-----------------------+
<=4 bytes=><===== len bytes ======>


 
* Packet layout : this describes the general layout of every packet.
<=========== HEADER ===========><==================PAYLOAD/DATA=======================> 
<================================ N bytes ============================================>  N = payload_len + header_len
+-----------------+------------+------------------------------------------------------+
| Int payload_len | Byte type  |                 Byte[] packet payload                |
+-----------------+------------+------------------------------------------------------+
<=====4 bytes=====><==1 byte==><=============== payload_len bytes ====================>  payload_len < 2^4 because it's encoded with 4 bytes

NOTE : each packet payload layout will vary according to the packet type.


* Types of packets

File packet:
	ID : 1
	Direction : Client <---> Server
	Description : This packet will encapsulate file transfered between client and server (For uploads and downloads)
 
'File' packet payload layout : 
<=========== payload_len bytes =========================> 
+----------------+--------------------------------------+
| Str fileName   | 		       Byte[] data 		        |
+----------------+--------------------------------------+
<====n bytes=====><======= payload_len-n bytes =========>


'Get' packet
	ID : 2
	Direction : Client ----> Server
	Description : this packet is sent by a client to request a file from the server

'Get' packet payload layout : 

<====== payload_len bytes ======>
+-------------------------------+
|           Str fileID          |
+-------------------------------+

 