addi $s0, $zero, 4		;	array:$s0 is address of array start from 4
lw $s1, 0($s0)				; size:$s1 = *array
addi $s0, $s0, 4			; array+=4
lw $t2, 0($s0)				; sum:$t2 = *array
addi $t0, $zero, 1		; i:$t0=1
beq $t0, $s1, 9				; --jmp=6*4=24 -> if(i==size) jumpTo end
NOP
NOP
addi $s0,$s0,4				; array+=4
NOP
NOP
lw $t1,0($s0)					; tmp:$t1=*array
add $t2,$t2,$t1				; sum+=$5
addi $t0,$t0,1				; i+=1
j 6									; jumpTo jmp
NOP										; --end=12*4=48--;
NOP
HALT
