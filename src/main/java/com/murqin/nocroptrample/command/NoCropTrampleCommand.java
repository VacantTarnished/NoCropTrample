package com.murqin.nocroptrample.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.murqin.nocroptrample.config.ModConfig;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jspecify.annotations.NonNull;
import net.minecraft.server.players.NameAndId;

/**
 * Handles the /nocroptrample command registration and execution.
 * <p>
 * Provides commands to view and modify trampling prevention settings.
 * </p>
 */
public class NoCropTrampleCommand {

        // Constants for permissions
        private static final int REQUIRED_OP_LEVEL = 2;

        // State constants
        private static final String STATE_ON = "on";
        private static final String STATE_OFF = "off";

        // Message formatting constants
        private static final String PREFIX = "§6[NoCropTrample] ";
        private static final String PREFIX_ERROR = "§c[NoCropTrample] ";
        private static final String LABEL_STATUS = "§fStatus:";
        private static final String LABEL_PLAYER = "§7Player trampling prevention: ";
        private static final String LABEL_MOB = "§7Mob trampling prevention: ";
        private static final String MSG_INVALID_STATE = "Invalid state! Use 'on' or 'off'.";
        private static final String MSG_CONFIG_RELOADED = "§aConfig reloaded!";

        /**
         * Registers the /nocroptrample command with all its subcommands.
         *
         * @param dispatcher the command dispatcher to register commands with
         */
        public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
                dispatcher.register(
                                Commands.literal("nocroptrample")
                                                // /nocroptrample - show status
                                                .executes(NoCropTrampleCommand::showStatus)

                                                // /nocroptrample player [on|off]
                                                .then(buildPlayerCommand())

                                                // /nocroptrample mob [on|off]
                                                .then(buildMobCommand())

                                                // /nocroptrample status
                                                .then(Commands.literal("status")
                                                                .executes(NoCropTrampleCommand::showStatus))

                                                // /nocroptrample reload
                                                .then(Commands.literal("reload")
                                                                .requires(source -> checkPermission(source,
                                                                                REQUIRED_OP_LEVEL))
                                                                .executes(NoCropTrampleCommand::reloadConfig)));
        }

        /**
         * Builds the player subcommand.
         *
         * @return the player command builder
         */
        private static LiteralArgumentBuilder<CommandSourceStack> buildPlayerCommand() {
                return Commands.literal("player")
                                .executes(NoCropTrampleCommand::showPlayerStatus)
                                .then(Commands.argument("state", StringArgumentType.word())
                                                .requires(source -> checkPermission(source, REQUIRED_OP_LEVEL))
                                                .suggests((context, builder) -> {
                                                        builder.suggest(STATE_ON);
                                                        builder.suggest(STATE_OFF);
                                                        return builder.buildFuture();
                                                })
                                                .executes(context -> setState(
                                                                context,
                                                                StringArgumentType.getString(context, "state"),
                                                                true)));
        }

        /**
         * Builds the mob subcommand.
         *
         * @return the mob command builder
         */
        private static LiteralArgumentBuilder<CommandSourceStack> buildMobCommand() {
                return Commands.literal("mob")
                                .executes(NoCropTrampleCommand::showMobStatus)
                                .then(Commands.argument("state", StringArgumentType.word())
                                                .requires(source -> checkPermission(source, REQUIRED_OP_LEVEL))
                                                .suggests((context, builder) -> {
                                                        builder.suggest(STATE_ON);
                                                        builder.suggest(STATE_OFF);
                                                        return builder.buildFuture();
                                                })
                                                .executes(context -> setState(
                                                                context,
                                                                StringArgumentType.getString(context, "state"),
                                                                false)));
        }

        /**
         * Checks if the command source has the required permission level.
         *
         * @param source the command source
         * @param level  the required permission level
         * @return true if the source has permission, false otherwise
         */
        private static boolean checkPermission(CommandSourceStack source, int level) {
                if (source.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                        return source.getServer().getPlayerList().isOp(new NameAndId(player.getGameProfile()));
                }
                return true;
        }

        /**
         * Shows the full status of all trampling prevention settings.
         *
         * @param context the command context
         * @return command result code (1 for success)
         */
        private static int showStatus(CommandContext<CommandSourceStack> context) {
                CommandSourceStack source = context.getSource();

                source.sendSuccess(() -> Component.literal(PREFIX + LABEL_STATUS), false);
                source.sendSuccess(() -> Component.literal("  " + LABEL_PLAYER)
                                .append(getStatusText(ModConfig.isPreventPlayerTrampling())), false);
                source.sendSuccess(() -> Component.literal("  " + LABEL_MOB)
                                .append(getStatusText(ModConfig.isPreventMobTrampling())), false);

                return 1;
        }

        /**
         * Shows only the player trampling prevention status.
         *
         * @param context the command context
         * @return command result code (1 for success)
         */
        private static int showPlayerStatus(CommandContext<CommandSourceStack> context) {
                CommandSourceStack source = context.getSource();
                source.sendSuccess(
                                () -> Component.literal(PREFIX + LABEL_PLAYER)
                                                .append(getStatusText(ModConfig.isPreventPlayerTrampling())),
                                false);
                return 1;
        }

        /**
         * Shows only the mob trampling prevention status.
         *
         * @param context the command context
         * @return command result code (1 for success)
         */
        private static int showMobStatus(CommandContext<CommandSourceStack> context) {
                CommandSourceStack source = context.getSource();
                source.sendSuccess(
                                () -> Component.literal(PREFIX + LABEL_MOB)
                                                .append(getStatusText(ModConfig.isPreventMobTrampling())),
                                false);
                return 1;
        }

        /**
         * Sets the trampling prevention state for either players or mobs.
         *
         * @param context  the command context
         * @param state    the state to set ("on" or "off")
         * @param isPlayer true for player setting, false for mob setting
         * @return command result code (1 for success, 0 for failure)
         */
        private static int setState(CommandContext<CommandSourceStack> context, String state, boolean isPlayer) {
                CommandSourceStack source = context.getSource();

                if (!state.equalsIgnoreCase(STATE_ON) && !state.equalsIgnoreCase(STATE_OFF)) {
                        source.sendFailure(Component.literal(PREFIX_ERROR + MSG_INVALID_STATE));
                        return 0;
                }

                boolean newState = state.equalsIgnoreCase(STATE_ON);
                String label;

                if (isPlayer) {
                        ModConfig.setPreventPlayerTrampling(newState);
                        label = LABEL_PLAYER;
                } else {
                        ModConfig.setPreventMobTrampling(newState);
                        label = LABEL_MOB;
                }

                source.sendSuccess(
                                () -> Component.literal(PREFIX + label)
                                                .append(getStatusText(newState)),
                                true);

                return 1;
        }

        /**
         * Reloads the configuration from disk.
         *
         * @param context the command context
         * @return command result code (1 for success)
         */
        private static int reloadConfig(CommandContext<CommandSourceStack> context) {
                CommandSourceStack source = context.getSource();

                ModConfig.load();

                source.sendSuccess(() -> Component.literal(PREFIX + MSG_CONFIG_RELOADED), true);

                return 1;
        }

        /**
         * Creates a colored component showing ON (green) or OFF (red).
         *
         * @param enabled the state to display
         * @return formatted component
         */
        private static @NonNull Component getStatusText(boolean enabled) {
                return enabled
                                ? Component.literal("ON").withStyle(ChatFormatting.GREEN)
                                : Component.literal("OFF").withStyle(ChatFormatting.RED);
        }
}
