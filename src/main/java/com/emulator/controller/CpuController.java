package com.emulator.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cpu")
@CrossOrigin(origins = "*") 
public class CpuController {

    // State variables to remember execution between STEP clicks
    private Map<String, Integer> regs = new HashMap<>();
    private int pc = 0;
    private int flagZ = 0;
    private int flagS = 0;
    private int flagC = 0;
    private int flagO = 0;
    private Map<String, String> memory = new HashMap<>();
    private String lastInstructions = "";

    public CpuController() {
        resetState();
    }

    private void resetState() {
        regs.put("A", 0); regs.put("B", 0); regs.put("C", 0); regs.put("D", 0);
        pc = 0;
        flagZ = 0; flagS = 0; flagC = 0; flagO = 0;
        memory.clear();
    }

    @PostMapping("/execute")
    public Map<String, Object> executeInstructions(@RequestBody Map<String, Object> payload) {
        String instructions = (String) payload.getOrDefault("instructions", "");
        boolean stepMode = (Boolean) payload.getOrDefault("stepMode", false);
        boolean resetRequested = (Boolean) payload.getOrDefault("reset", false);

        System.out.println("Received Assembly: \n" + instructions);
        
        String[] lines = instructions.split("\\r?\\n");
        
        // Reset state if RESET button clicked, code changed, or we already finished executing
        if (resetRequested || !instructions.equals(lastInstructions) || pc >= lines.length) {
            resetState();
            lastInstructions = instructions;
            
            // If it was just a reset request, return the cleared state immediately
            if (resetRequested) {
                return buildResponse();
            }
        }

        // Determine how many instructions to run (1 for STEP, all remaining for RUN)
        int linesToExecute = stepMode ? 1 : lines.length - pc;

        for (int i = 0; i < linesToExecute && pc < lines.length; ) {
            String line = lines[pc].trim().toUpperCase();
            
            // Skip empty lines and comments, don't count them as a step
            if (line.isEmpty() || line.startsWith(";")) { 
                pc++; 
                continue; 
            }
            
            memory.put(String.format("0x%04X", pc), line.split(" ")[0]);
            
            if (line.equals("HLT")) { pc++; break; }
            
            if (line.startsWith("MOV") || line.startsWith("ADD") || line.startsWith("SUB")) {
                String[] args = line.substring(3).split(",");
                if (args.length == 2) {
                    String dest = args[0].trim();
                    String src = args[1].trim();
                    
                    int val;
                    if (regs.containsKey(src)) {
                        val = regs.get(src);
                    } else if (src.startsWith("0X")) {
                        val = Integer.parseInt(src.substring(2), 16);
                    } else {
                        val = Integer.parseInt(src);
                    }
                    
                    if (line.startsWith("MOV")) {
                        int result = val & 0xFF;
                        regs.put(dest, result);
                        
                        // MOV typically updates the Zero and Sign flags, but not Carry or Overflow
                        flagZ = (result == 0) ? 1 : 0;
                        flagS = ((result & 0x80) != 0) ? 1 : 0;
                    } else if (line.startsWith("ADD")) {
                        int regVal = regs.get(dest);
                        int result = regVal + val;
                        int result8Bit = result & 0xFF; // Constrain to 8 bits
                        
                        regs.put(dest, result8Bit);
                        
                        // Calculate all flags for ADD
                        flagC = (result > 255) ? 1 : 0;
                        flagZ = (result8Bit == 0) ? 1 : 0;
                        flagS = ((result8Bit & 0x80) != 0) ? 1 : 0;
                        flagO = (((regVal ^ result8Bit) & (val ^ result8Bit) & 0x80) != 0) ? 1 : 0;
                    } else if (line.startsWith("SUB")) {
                        int regVal = regs.get(dest);
                        int result = regVal - val;
                        int result8Bit = result & 0xFF; // Constrain to 8 bits
                        
                        regs.put(dest, result8Bit);
                        
                        // Calculate all flags for SUB
                        flagC = (regVal < val) ? 1 : 0; // Carry acts as a Borrow flag in subtraction
                        flagZ = (result8Bit == 0) ? 1 : 0;
                        flagS = ((result8Bit & 0x80) != 0) ? 1 : 0;
                        flagO = (((regVal ^ val) & (regVal ^ result8Bit) & 0x80) != 0) ? 1 : 0;
                    }
                }
            }
            pc++;
            i++; // Increment executed instructions count
        }

        return buildResponse();
    }

    private Map<String, Object> buildResponse() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> outRegs = new HashMap<>();
        for (String reg : regs.keySet()) { outRegs.put(reg, String.format("0x%02X", regs.get(reg))); }
        outRegs.put("PC", String.format("0x%04X", pc));
        outRegs.put("SP", "0xFFFF");
        
        response.put("registers", outRegs);
        
        String flagsStr = String.format("Z=%d S=%d C=%d O=%d", flagZ, flagS, flagC, flagO);
        response.put("flags", flagsStr);
        response.put("memory", memory);

        return response;
    }
}