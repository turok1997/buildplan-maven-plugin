package com.github.jcgay.maven.plugin.buildplan;

import com.github.jcgay.maven.plugin.buildplan.display.ListPluginTableDescriptor;
import com.github.jcgay.maven.plugin.buildplan.display.MojoExecutionDisplay;
import com.github.jcgay.maven.plugin.buildplan.display.TableDescriptor;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Collection;
import java.util.Map;

/**
 * List plugin executions by plugin for the current project.
 */
@Mojo(name = "list-plugin",
      threadSafe = true,
      requiresProject = true)
public class ListPluginMojo extends AbstractLifecycleMojo {

    /** Display plugin executions only for the specified plugin. */
    @Parameter(property = "buildplan.plugin")
    private String plugin;

    public void execute() throws MojoExecutionException, MojoFailureException {

        Multimap<String,MojoExecution> plan = Groups.ByPlugin.of(calculateExecutionPlan().getMojoExecutions(), plugin);

        if (plan.isEmpty()) {
            getLog().warn("No plugin found with artifactId: " + plugin);
        } else {
            TableDescriptor descriptor = ListPluginTableDescriptor.of(plan.values());
            for (Map.Entry<String, Collection<MojoExecution>> executions : plan.asMap().entrySet()) {
                getLog().info(pluginTitleLine(descriptor, executions.getKey()));
                for (MojoExecution execution : executions.getValue()) {
                    getLog().info(line(descriptor.rowFormat(), execution));
                }
            }
        }
    }

    private String line(String rowFormat, MojoExecution execution) {

        MojoExecutionDisplay display = new MojoExecutionDisplay(execution);

        return String.format(rowFormat, display.getPhase(),
                                        display.getExecutionId(),
                                        display.getGoal());
    }

    private String pluginTitleLine(TableDescriptor descriptor, String key) {
        return key + " " + Strings.repeat("-", descriptor.width() - key.length());
    }
}
