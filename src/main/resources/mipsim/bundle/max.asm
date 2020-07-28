addi $1,$zero,4  ;array address
lw $2,0($1)			 ;
addi $1,$1,4
lw $3,0($1)
addi $4,$zero,1
beq $4,$2,8
addi $1,$1,4
lw $4,0($1)
slt $7,$3,$4
beq $7,$zero,1
addi $3,$4,0
addi $2,$2,1
j 6
NOP
NOP
NOP
NOP

