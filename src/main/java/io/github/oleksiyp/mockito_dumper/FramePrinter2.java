package io.github.oleksiyp.mockito_dumper;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.analysis.Analyzer;
import javassist.bytecode.analysis.Frame;
import javassist.bytecode.analysis.FramePrinter;
import javassist.bytecode.analysis.Type;

import java.io.PrintStream;

public class FramePrinter2 {
    private PrintStream stream;

    public FramePrinter2(PrintStream stream) {
        this.stream = stream;
    }

    public void print(CtClass clazz) {
        CtConstructor[] constructors = clazz.getDeclaredConstructors();
        for (int i = 0; i < constructors.length; i++) {
            print(constructors[i]);
        }
        CtMethod[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            print(methods[i]);
        }
    }

    public void print(CtBehavior method) {
        stream.println("\n" + getMethodString(method));
        MethodInfo info = method.getMethodInfo2();
        ConstPool pool = info.getConstPool();
        CodeAttribute code = info.getCodeAttribute();
        if (code == null)
            return;

        Frame[] frames;
        try {
            frames = (new Analyzer()).analyze(method.getDeclaringClass(), info);
        } catch (BadBytecode e) {
            throw new RuntimeException(e);
        }

        int spacing = String.valueOf(code.getCodeLength()).length();

        CodeIterator iterator = code.iterator();
        while (iterator.hasNext()) {
            int pos;
            try {
                pos = iterator.next();
            } catch (BadBytecode e) {
                throw new RuntimeException(e);
            }

            stream.println(pos + ": " + InstructionPrinter.instructionString(iterator, pos, pool));

            addSpacing(spacing + 3);
            Frame frame = frames[pos];
            if (frame == null) {
                stream.println("--DEAD CODE--");
                continue;
            }
            printStack(frame);

            addSpacing(spacing + 3);
            printLocals(frame);
        }

    }

    private String getMethodString(CtBehavior method) {
        try {
            return Modifier.toString(method.getModifiers()) + " "
                    + (method instanceof CtMethod ? ((CtMethod)method).getReturnType().getName() + " " : "") +
                    method.getName()
                    + Descriptor.toString(method.getSignature()) + ";";
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void addSpacing(int count) {
        while (count-- > 0)
            stream.print(' ');
    }


    private void printStack(Frame frame) {
        stream.print("stack [");
        int top = frame.getTopIndex();
        for (int i = 0; i <= top; i++) {
            if (i > 0)
                stream.print(", ");
            Type type = frame.getStack(i);
            stream.print(type);
        }
        stream.println("]");
    }

    private void printLocals(Frame frame) {
        stream.print("locals [");
        int length = frame.localsLength();
        for (int i = 0; i < length; i++) {
            if (i > 0)
                stream.print(", ");
            Type type = frame.getLocal(i);
            stream.print(type == null ? "empty" : type.toString());
        }
        stream.println("]");
    }
}
