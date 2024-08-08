package com.daladirn.bpmmod.command;

import com.daladirn.bpmmod.BpmMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.List;

public class BpmCommand extends CommandBase {
    private final BpmMod bpmMod;
    public BpmCommand(BpmMod bpmMod) {
        this.bpmMod = bpmMod;
    }


    @Override
    public String getCommandName() {
        return "bpm";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true; // return true otherwise you won't be able to use the command
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reset")) {
            BpmMod.bpmTracker.reset();
        } else if (args.length > 0 && args[0].equalsIgnoreCase("enable")) {
            bpmMod.enable();
        } else if (args.length > 0 && args[0].equalsIgnoreCase("disable")) {
            bpmMod.disable();
        }

        if (BpmMod.isEnabled) {
            if (args.length > 1 && args[0].equalsIgnoreCase("mouse") && args[1].equalsIgnoreCase("lock")) {
                if (!BpmMod.bpmMouseLock.isMouseLocked) BpmMod.bpmMouseLock.toggle();
            } else if (args.length > 1 && args[0].equalsIgnoreCase("mouse") && args[1].equalsIgnoreCase("unlock")) {
                if (BpmMod.bpmMouseLock.isMouseLocked) BpmMod.bpmMouseLock.toggle();
            }
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        // when you type the command and press tab complete,
        // this method will allow you to cycle through the words that match what you typed
        final String[] options = new String[]{"reset"};
        return getListOfStringsMatchingLastWord(args, options);
    }

}