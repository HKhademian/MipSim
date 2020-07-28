addi $1,$zero,0
lw $2,0($1)
addi $1,$1,4
lw $3,0($1)
addi $4,$zero,1
beq $4,$2,11
addi $1,$1,4
lw $4,0($1)
j 13
addi $3,$4,$3
addi $2,$2,1
j 6
addi $7,$zero,0
beq  $7,$4,-4
addi $3,$3,$3
addi $7,$7,1
j 14
NOP
NOP
NOP
NOP
