addi $1,$zero,20			;	array:$1 is address of ??
lw $2,0($1)						; size:$2 = *array
addi $1,$1,4					; array+=4
lw $3,0($1)						; sum:$3 = *array
addi $4,$zero,1				; i:$4=1
beq $4,$2,5						; --jmp=6*4=24 -> if(i==size) jumpTo end
addi $1,$1,4					; array+=4
lw $5,0($1)						; $5=*array
add $3,$3,$5					; sum+=$5
addi $2,$2,1					; i+=1
j 24									; jumpTo jmp
NOP										; --end=12*4=48--;
