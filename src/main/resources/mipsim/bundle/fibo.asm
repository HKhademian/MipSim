addi $1,$zero,24
lw 	 $2,0($1)
addi $3,$zero,0
addi $4,$zero,0
addi $5,$zero,1
addi $6,$zero,0
beq  $3,$2,5
add $6,$5,$4
addi $4,$5,0
addi $5,$6,0
addi $3,$3,1
j 7
NOP
NOP
NOP
NOP
halt
