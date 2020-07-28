addi $1,$zero,4  ;	array:$s0 is address of array start from 4
addi $2,$zero,0	 ;  i for count
lw $4,0($1)      ;  size of array
addi $4,$4,-1		 ;
beq $2,$4,21		 ;
addi $3,$zero,0  ;  j count
addi $9,$4,-1		 ;
sub  $9,$9,$2		 ;
beq  $3,$9,15		 ;
addi $1,$1,4		 ;
lw $5,0($1)			 ;
addi $1,$1,4
lw $6,0($1)
slt $7,$6,$5
beq $7,$zero,3
addi $8,$6,0
addi $6,$5,0
addi $5,$8,0
addi $1,$1,-4
sw $5,0($1)
addi$1,$1,4
sw $6,0($1)
addi $3,$3,1
j 9
addi $2,$2,1
j 5
NOP
NOP
NOP
NOP
NOP
NOP
halt
