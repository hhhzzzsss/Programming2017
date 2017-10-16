def ternary(num):
        val = range(8)
        t=num;
        for i in range(0,8):
                val[7-i] = t % 3
                t = t / 3
        return val

for i in range(6561):
        expr = "1"
        permutation = ternary(i)
        for j in range(1,9):
                if permutation[j-1]==1:
                        expr += "+"
                elif permutation[j-1]==2:
                        expr += "-"
                expr += str(j+1);
        if (eval(expr)==100):
                print(expr)
